package com.dataagent.jpa.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for Data Agent JPA module.
 * These properties can be set in application.yml or application.properties.
 */
@ConfigurationProperties(prefix = "data-agent.jpa")
data class DataAgentJpaProperties(
    /**
     * Whether to enable automatic entity discovery on startup
     */
    var autoDiscover: Boolean = true,
    
    /**
     * Packages to scan for JPA entities. Multiple packages can be specified as comma-separated values.
     * Example: "com.example.domain,com.example.entities"
     * 
     * Priority order:
     * 1. This configuration property
     * 2. @EnableDataAgent(packages = "...") annotation
     * 3. Inferred from main application class
     */
    var scanPackages: String = "",
    
    /**
     * Startup delay in milliseconds before starting schema discovery
     */
    var startupDelay: Long = 1000L,
    
    /**
     * Whether to cache discovered schemas
     */
    var cacheSchemas: Boolean = true,
    
    /**
     * Cache TTL in seconds
     */
    var cacheTtl: Long = 3600L,
    
    /**
     * Maximum number of entities to discover
     */
    var maxEntities: Int = 1000,
    
    /**
     * Whether to enable debug logging
     */
    var debug: Boolean = false
)
