package com.dataagent.core.service

import com.dataagent.core.model.EntitySchema

/**
 * Generic interface for schema discovery services.
 * Different implementations can be provided for different data access technologies
 * (e.g., JPA, MongoDB, etc.).
 */
interface SchemaDiscoveryService {
    
    /**
     * Discovers all entities in the given package.
     */
    fun discoverEntities(packageName: String): List<EntitySchema>
    
    /**
     * Discovers the schema for a specific entity class.
     */
    fun discoverEntitySchema(entityClass: Class<*>): EntitySchema
} 