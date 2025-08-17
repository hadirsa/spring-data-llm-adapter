package com.dataagent.jpa.annotation

/**
 * Annotation to mark JPA entities that should be learned by the Data Agent.
 * This annotation enables the Data Agent to automatically discover and learn
 * the database schema structure for the annotated entity.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DataAgent(
    /**
     * Optional description of the entity for better understanding
     */
    val description: String = "",
    
    /**
     * Whether to include this entity in automatic schema discovery
     */
    val discoverable: Boolean = true,
    
    /**
     * Custom table name if different from the entity class name
     */
    val tableName: String = ""
) 