package ai.hadirsa.spring.data.llm.adapter.service

import ai.hadirsa.spring.data.llm.adapter.model.EntitySchema
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

/**
 * Main service for the Data Agent that provides intelligent data access
 * and schema learning capabilities.
 * This service is generic and can work with different schema discovery implementations.
 */
@Service
class DataAgentService @Autowired constructor(
    private val schemaRegistryService: SchemaRegistryService
) {
    
    private val logger = LoggerFactory.getLogger(DataAgentService::class.java)
    
    /**
     * Discovers and learns schemas from entities in the specified package using the provided discovery service.
     */
    fun discoverAndLearnSchemas(packageName: String, discoveryService: SchemaDiscoveryService): List<EntitySchema> {
        logger.info("Starting schema discovery and learning in package: $packageName")
        
        try {
            val discoveredSchemas = discoveryService.discoverEntities(packageName)
            schemaRegistryService.registerSchemas(discoveredSchemas)
            
            logger.info("Successfully discovered and learned ${discoveredSchemas.size} entity schemas")
            return discoveredSchemas
            
        } catch (e: Exception) {
            logger.error("Failed to discover and learn schemas in package: $packageName", e)
            throw RuntimeException("Schema discovery failed", e)
        }
    }
    
    /**
     * Gets all learned schemas.
     */
    fun getAllLearnedSchemas(): List<EntitySchema> {
        return schemaRegistryService.getAllSchemas()
    }
    
    /**
     * Gets a specific schema by entity class name.
     */
    fun getSchema(entityClassName: String): EntitySchema? {
        return schemaRegistryService.getSchemaByClassName(entityClassName)
    }
    
    /**
     * Gets a specific schema by table name.
     */
    fun getSchemaByTable(tableName: String): EntitySchema? {
        return schemaRegistryService.getSchemaByTableName(tableName)
    }
    
    /**
     * Gets a summary of all learned schemas.
     */
    fun getSchemaSummary(): Map<String, String> {
        return schemaRegistryService.getSchemaSummary()
    }
    
    /**
     * Checks if the Data Agent has learned schemas for the given entity.
     */
    fun hasLearnedSchema(entityClassName: String): Boolean {
        return schemaRegistryService.hasSchema(entityClassName)
    }
    
    /**
     * Gets the total number of learned schemas.
     */
    fun getLearnedSchemaCount(): Int {
        return schemaRegistryService.getSchemaCount()
    }
    
    /**
     * Manually learn a specific entity schema using the provided discovery service.
     */
    fun learnEntitySchema(entityClass: Class<*>, discoveryService: SchemaDiscoveryService): EntitySchema {
        logger.info("Manually learning schema for entity: ${entityClass.name}")
        
        val schema = discoveryService.discoverEntitySchema(entityClass)
        schemaRegistryService.registerSchema(schema)
        
        logger.info("Successfully learned schema for entity: ${entityClass.name}")
        return schema
    }
    
    /**
     * Clears all learned schemas.
     */
    fun clearLearnedSchemas() {
        logger.info("Clearing all learned schemas")
        schemaRegistryService.clearSchemas()
    }
    
    /**
     * Gets information about a specific entity including its fields and relationships.
     */
    fun getEntityInfo(entityClassName: String): String? {
        val schema = getSchema(entityClassName)
        if (schema == null) {
            return null
        }
        
        return buildString {
            appendLine("Entity: ${schema.entityClassName}")
            appendLine("Table: ${schema.tableName}")
            if (schema.description.isNotEmpty()) {
                appendLine("Description: ${schema.description}")
            }
            appendLine("Fields (${schema.fields.size}):")
            schema.fields.forEach { field ->
                appendLine("  - ${field.fieldName} (${field.columnName}): ${field.fieldType}${if (field.isPrimaryKey) " [PK]" else ""}")
            }
            if (schema.relationships.isNotEmpty()) {
                appendLine("Relationships (${schema.relationships.size}):")
                schema.relationships.forEach { rel ->
                    appendLine("  - ${rel.fieldName}: ${rel.relationshipType} -> ${rel.targetEntity}")
                }
            }
        }
    }
    
    /**
     * Gets a list of all entity names that have been learned.
     */
    fun getLearnedEntityNames(): List<String> {
        return schemaRegistryService.getAllSchemas().map { it.entityClassName }
    }
    
    /**
     * Gets a list of all table names that have been learned.
     */
    fun getLearnedTableNames(): List<String> {
        return schemaRegistryService.getAllSchemas().map { it.tableName }
    }
    
    /**
     * Validates if the requested entity or table exists in the learned schemas.
     * Returns an error message if not found, null if found.
     */
    fun validateEntityOrTableExists(entityOrTableName: String): String? {
        val allSchemas = schemaRegistryService.getAllSchemas()
        
        // Check if it matches any entity class name
        val entityMatch = allSchemas.find { 
            it.entityClassName.equals(entityOrTableName, ignoreCase = true) ||
            it.entityClassName.endsWith(".$entityOrTableName", ignoreCase = true)
        }
        
        if (entityMatch != null) {
            return null // Entity found
        }
        
        // Check if it matches any table name
        val tableMatch = allSchemas.find { 
            it.tableName.equals(entityOrTableName, ignoreCase = true)
        }
        
        if (tableMatch != null) {
            return null // Table found
        }
        
        // Check if it's a partial match for any entity or table
        val partialEntityMatch = allSchemas.find { 
            it.entityClassName.contains(entityOrTableName, ignoreCase = true) ||
            it.tableName.contains(entityOrTableName, ignoreCase = true)
        }
        
        if (partialEntityMatch != null) {
            return "No exact match found for '$entityOrTableName'. Did you mean '${partialEntityMatch.entityClassName}' or '${partialEntityMatch.tableName}'?"
        }
        
        // No match found at all
        val availableEntities = allSchemas.map { it.entityClassName }
        val availableTables = allSchemas.map { it.tableName }
        
        return "No '$entityOrTableName' related table exists in the provided schema. " +
               "Available entities: ${availableEntities.joinToString(", ")}. " +
               "Available tables: ${availableTables.joinToString(", ")}."
    }
    
    /**
     * Validates if multiple requested entities or tables exist in the learned schemas.
     * Returns a map of entity/table names to error messages for those that don't exist.
     */
    fun validateEntitiesOrTablesExist(entityOrTableNames: List<String>): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        
        entityOrTableNames.forEach { name ->
            val error = validateEntityOrTableExists(name)
            if (error != null) {
                errors[name] = error
            }
        }
        
        return errors
    }
    
    /**
     * Gets a list of all available entity and table names for autocomplete or validation purposes.
     */
    fun getAvailableEntityAndTableNames(): Map<String, List<String>> {
        val allSchemas = schemaRegistryService.getAllSchemas()
        return mapOf(
            "entities" to allSchemas.map { it.entityClassName },
            "tables" to allSchemas.map { it.tableName },
            "descriptions" to allSchemas.map { "${it.entityClassName} (${it.description})" }
        )
    }
} 
