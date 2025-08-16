package com.dataagent.core.service

import com.dataagent.core.model.EntitySchema
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Service responsible for storing and managing discovered entity schemas.
 * This service acts as a central registry for all learned schemas.
 */
@Service
class SchemaRegistryService {
    
    private val logger = LoggerFactory.getLogger(SchemaRegistryService::class.java)
    
    /**
     * Registry to store discovered schemas by entity class name
     */
    private val schemaRegistry = ConcurrentHashMap<String, EntitySchema>()
    
    /**
     * Registry to store schemas by table name
     */
    private val tableRegistry = ConcurrentHashMap<String, EntitySchema>()
    
    /**
     * Registers a discovered entity schema.
     */
    fun registerSchema(schema: EntitySchema) {
        logger.info("Registering schema for entity: ${schema.entityClassName}")
        
        schemaRegistry[schema.entityClassName] = schema
        tableRegistry[schema.tableName] = schema
        
        logger.debug("Schema registered successfully. Total schemas: ${schemaRegistry.size}")
    }
    
    /**
     * Registers multiple schemas at once.
     */
    fun registerSchemas(schemas: List<EntitySchema>) {
        logger.info("Registering ${schemas.size} schemas")
        schemas.forEach { registerSchema(it) }
    }
    
    /**
     * Retrieves a schema by entity class name.
     */
    fun getSchemaByClassName(className: String): EntitySchema? {
        return schemaRegistry[className]
    }
    
    /**
     * Retrieves a schema by table name.
     */
    fun getSchemaByTableName(tableName: String): EntitySchema? {
        return tableRegistry[tableName]
    }
    
    /**
     * Retrieves all registered schemas.
     */
    fun getAllSchemas(): List<EntitySchema> {
        return schemaRegistry.values.toList()
    }
    
    /**
     * Retrieves schemas by entity class names.
     */
    fun getSchemasByClassNames(classNames: List<String>): List<EntitySchema> {
        return classNames.mapNotNull { getSchemaByClassName(it) }
    }
    
    /**
     * Checks if a schema exists for the given entity class name.
     */
    fun hasSchema(className: String): Boolean {
        return schemaRegistry.containsKey(className)
    }
    
    /**
     * Checks if a schema exists for the given table name.
     */
    fun hasTableSchema(tableName: String): Boolean {
        return tableRegistry.containsKey(tableName)
    }
    
    /**
     * Gets the total number of registered schemas.
     */
    fun getSchemaCount(): Int {
        return schemaRegistry.size
    }
    
    /**
     * Clears all registered schemas.
     */
    fun clearSchemas() {
        logger.info("Clearing all registered schemas")
        schemaRegistry.clear()
        tableRegistry.clear()
    }
    
    /**
     * Removes a specific schema by entity class name.
     */
    fun removeSchema(className: String): EntitySchema? {
        val schema = schemaRegistry.remove(className)
        if (schema != null) {
            tableRegistry.remove(schema.tableName)
            logger.info("Removed schema for entity: $className")
        }
        return schema
    }
    
    /**
     * Gets a summary of all registered schemas.
     */
    fun getSchemaSummary(): Map<String, String> {
        return schemaRegistry.values.associate { schema ->
            schema.entityClassName to "${schema.tableName} (${schema.fields.size} fields, ${schema.relationships.size} relationships)"
        }
    }
} 