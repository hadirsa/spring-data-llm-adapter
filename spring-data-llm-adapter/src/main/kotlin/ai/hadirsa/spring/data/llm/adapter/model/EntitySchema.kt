package ai.hadirsa.spring.data.llm.adapter.model

import java.time.LocalDateTime

/**
 * Represents the learned schema information for an entity.
 */
data class EntitySchema(
    /**
     * The entity class name
     */
    val entityClassName: String,
    
    /**
     * The table name in the database
     */
    val tableName: String,
    
    /**
     * Description of the entity (from @DataAgent annotation)
     */
    val description: String,
    
    /**
     * List of fields/columns in the entity
     */
    val fields: List<FieldSchema>,
    
    /**
     * List of relationships with other entities
     */
    val relationships: List<RelationshipSchema>,
    
    /**
     * When this schema was discovered
     */
    val discoveredAt: LocalDateTime = LocalDateTime.now(),
    
    /**
     * The source entity class
     */
    val sourceClass: Class<*>
)

/**
 * Represents a field/column in an entity.
 */
data class FieldSchema(
    /**
     * Field name
     */
    val fieldName: String,
    
    /**
     * Column name in database
     */
    val columnName: String,
    
    /**
     * Field type
     */
    val fieldType: String,
    
    /**
     * Database column type
     */
    val columnType: String,
    
    /**
     * Whether the field is nullable
     */
    val nullable: Boolean,
    
    /**
     * Whether the field is unique
     */
    val unique: Boolean,
    
    /**
     * Whether the field is part of primary key
     */
    val isPrimaryKey: Boolean,
    
    /**
     * Field length (for String fields)
     */
    val length: Int?,
    
    /**
     * Field precision (for numeric fields)
     */
    val precision: Int?,
    
    /**
     * Field scale (for numeric fields)
     */
    val scale: Int?,
    
    /**
     * Human-readable description of the field
     */
    val description: String = "",
    
    /**
     * Example values for this field (free-form string)
     */
    val examples: String = "",
    
    /**
     * Business category or domain this field belongs to
     */
    val category: String = ""
)

/**
 * Represents a relationship between entities.
 */
data class RelationshipSchema(
    /**
     * Field name that contains the relationship
     */
    val fieldName: String,
    
    /**
     * Target entity class name
     */
    val targetEntity: String,
    
    /**
     * Relationship type (OneToOne, OneToMany, ManyToOne, ManyToMany)
     */
    val relationshipType: String,
    
    /**
     * Whether the relationship is mapped by another entity
     */
    val mappedBy: String?,
    
    /**
     * Join column name if applicable
     */
    val joinColumn: String?,
    
    /**
     * Cascade types
     */
    val cascadeTypes: List<String>,
    
    /**
     * Fetch type (LAZY, EAGER)
     */
    val fetchType: String
) 
