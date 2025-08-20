package com.dataagent.jpa.service

import com.dataagent.core.service.SchemaDiscoveryService
import com.dataagent.core.annotation.DataAgent
import com.dataagent.core.annotation.DataAgentField
import com.dataagent.core.model.EntitySchema
import com.dataagent.core.model.FieldSchema
import com.dataagent.core.model.RelationshipSchema
import jakarta.persistence.*
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * JPA-specific service responsible for discovering and learning entity schemas
 * from classes annotated with @DataAgent.
 */
class JpaSchemaDiscoveryService : SchemaDiscoveryService {
    
    private val logger = LoggerFactory.getLogger(JpaSchemaDiscoveryService::class.java)
    
    /**
     * Discovers all entities annotated with @DataAgent in the given package.
     */
    override fun discoverEntities(basePackage: String): List<EntitySchema> {
        logger.info("Starting JPA entity discovery in package: $basePackage")
        
        val reflections = Reflections(basePackage, Scanners.TypesAnnotated)
        val types = reflections.getTypesAnnotatedWith(DataAgent::class.java)
        logger.info("Found ${types.size} candidate JPA entities")
        return types.mapNotNull { clazz ->
            try {
                discoverEntitySchema(clazz)
            } catch (e: Exception) {
                logger.error("Failed to discover JPA entity: ${clazz.name}", e)
                null
            }
        }
    }
    
    /**
     * Discovers the schema for a specific JPA entity class.
     */
    override fun discoverEntitySchema(entityClass: Class<*>): EntitySchema {
        logger.debug("Discovering JPA schema for entity: ${entityClass.name}")
        
        val dataAgentAnnotation = entityClass.getAnnotation(DataAgent::class.java)
        val tableAnnotation = entityClass.getAnnotation(Table::class.java)
        
        val tableName = when {
            dataAgentAnnotation?.tableName?.isNotEmpty() == true -> dataAgentAnnotation.tableName
            tableAnnotation?.name?.isNotEmpty() == true -> tableAnnotation.name
            else -> entityClass.simpleName.uppercase()
        }
        
        val fields = discoverFields(entityClass)
        val relationships = discoverRelationships(entityClass)
        
        return EntitySchema(
            entityClassName = entityClass.name,
            tableName = tableName,
            description = dataAgentAnnotation?.description ?: "",
            fields = fields,
            relationships = relationships,
            sourceClass = entityClass
        )
    }
    
    private fun discoverFields(entityClass: Class<*>): List<FieldSchema> {
        val fields = mutableListOf<FieldSchema>()
        
        entityClass.declaredFields.forEach { field ->
            if (!Modifier.isStatic(field.modifiers)) {
                val fieldSchema = createFieldSchema(field)
                if (fieldSchema != null) {
                    fields.add(fieldSchema)
                }
            }
        }
        
        // Also check inherited fields
        var superClass = entityClass.superclass
        while (superClass != null && superClass != Any::class.java) {
            superClass.declaredFields.forEach { field ->
                if (!Modifier.isStatic(field.modifiers)) {
                    val fieldSchema = createFieldSchema(field)
                    if (fieldSchema != null) {
                        fields.add(fieldSchema)
                    }
                }
            }
            superClass = superClass.superclass
        }
        
        return fields
    }
    
    private fun createFieldSchema(field: Field): FieldSchema? {
        val columnAnnotation = field.getAnnotation(Column::class.java)
        val idAnnotation = field.getAnnotation(Id::class.java)
        val generatedValueAnnotation = field.getAnnotation(GeneratedValue::class.java)
        val dataAgentField = field.getAnnotation(DataAgentField::class.java)
        
        // Skip JPA relationship fields
        if (field.isAnnotationPresent(OneToOne::class.java) ||
            field.isAnnotationPresent(OneToMany::class.java) ||
            field.isAnnotationPresent(ManyToOne::class.java) ||
            field.isAnnotationPresent(ManyToMany::class.java)) {
            return null
        }
        
        val columnName = when {
            columnAnnotation?.name?.isNotEmpty() == true -> columnAnnotation.name
            else -> field.name.lowercase()
        }
        
        val fieldType = field.type.simpleName
        val columnType = determineColumnType(field.type)
        
        return FieldSchema(
            fieldName = field.name,
            columnName = columnName,
            fieldType = fieldType,
            columnType = columnType,
            nullable = columnAnnotation?.nullable ?: true,
            unique = columnAnnotation?.unique ?: false,
            isPrimaryKey = idAnnotation != null,
            length = columnAnnotation?.length?.takeIf { it > 0 },
            precision = columnAnnotation?.precision?.takeIf { it > 0 },
            scale = columnAnnotation?.scale?.takeIf { it > 0 },
            description = dataAgentField?.description ?: "",
            examples = dataAgentField?.examples ?: "",
            category = dataAgentField?.category ?: ""
        )
    }
    
    private fun discoverRelationships(entityClass: Class<*>): List<RelationshipSchema> {
        val relationships = mutableListOf<RelationshipSchema>()
        
        entityClass.declaredFields.forEach { field ->
            val relationship = createRelationshipSchema(field)
            if (relationship != null) {
                relationships.add(relationship)
            }
        }
        
        return relationships
    }
    
    private fun createRelationshipSchema(field: Field): RelationshipSchema? {
        val oneToOne = field.getAnnotation(OneToOne::class.java)
        val oneToMany = field.getAnnotation(OneToMany::class.java)
        val manyToOne = field.getAnnotation(ManyToOne::class.java)
        val manyToMany = field.getAnnotation(ManyToMany::class.java)
        
        val relationshipType = when {
            oneToOne != null -> "OneToOne"
            oneToMany != null -> "OneToMany"
            manyToOne != null -> "ManyToOne"
            manyToMany != null -> "ManyToMany"
            else -> null
        }
        
        if (relationshipType == null) return null
        
        val joinColumn = field.getAnnotation(JoinColumn::class.java)?.name
        
        val cascadeTypes = when {
            oneToOne != null -> oneToOne.cascade.map { it.name }
            oneToMany != null -> oneToMany.cascade.map { it.name }
            manyToOne != null -> manyToOne.cascade.map { it.name }
            manyToMany != null -> manyToMany.cascade.map { it.name }
            else -> emptyList()
        }
        
        val fetchType = when {
            oneToOne != null -> oneToOne.fetch.name
            oneToMany != null -> oneToMany.fetch.name
            manyToOne != null -> manyToOne.fetch.name
            manyToMany != null -> manyToMany.fetch.name
            else -> "LAZY"
        }
        
        val mappedBy = when {
            oneToOne != null -> oneToOne.mappedBy.takeIf { it.isNotEmpty() }
            oneToMany != null -> oneToMany.mappedBy.takeIf { it.isNotEmpty() }
            manyToOne != null -> null // ManyToOne doesn't have mappedBy
            manyToMany != null -> manyToMany.mappedBy.takeIf { it.isNotEmpty() }
            else -> null
        }
        
        val targetEntity = when {
            field.type.isArray -> field.type.componentType.name
            field.type.name.startsWith("java.util.List") -> {
                // Try to determine the generic type
                field.genericType.toString().let { genericType ->
                    if (genericType.contains("<")) {
                        genericType.substringAfter("<").substringBefore(">")
                    } else {
                        "Object"
                    }
                }
            }
            else -> field.type.name
        }
        
        return RelationshipSchema(
            fieldName = field.name,
            targetEntity = targetEntity,
            relationshipType = relationshipType,
            mappedBy = mappedBy,
            joinColumn = joinColumn,
            cascadeTypes = cascadeTypes,
            fetchType = fetchType
        )
    }
    
    private fun determineColumnType(fieldType: Class<*>): String {
        return when (fieldType) {
            String::class.java -> "VARCHAR"
            Int::class.java, Integer::class.java -> "INTEGER"
            Long::class.java, java.lang.Long::class.java -> "BIGINT"
            Double::class.java, java.lang.Double::class.java -> "DOUBLE"
            Float::class.java, java.lang.Float::class.java -> "FLOAT"
            Boolean::class.java, java.lang.Boolean::class.java -> "BOOLEAN"
            java.time.LocalDateTime::class.java -> "TIMESTAMP"
            java.time.LocalDate::class.java -> "DATE"
            java.time.LocalTime::class.java -> "TIME"
            java.math.BigDecimal::class.java -> "DECIMAL"
            java.math.BigInteger::class.java -> "BIGINT"
            else -> "VARCHAR"
        }
    }
} 
