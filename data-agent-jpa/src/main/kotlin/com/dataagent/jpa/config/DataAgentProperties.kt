package com.dataagent.jpa.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * JPA-specific configuration properties for the Data Agent.
 * These properties can be configured in application.yml or application.properties.
 */
@ConfigurationProperties(prefix = "dataagent.jpa")
data class DataAgentProperties(
    /**
     * Base package to scan for JPA entities annotated with @DataAgent.
     * If not specified, auto-discovery will be disabled.
     */
    var basePackage: String = "",
    
    /**
     * Whether to automatically discover JPA entities on startup.
     * Default: true
     */
    var autoDiscover: Boolean = true,
    
    /**
     * Whether to automatically generate repositories for discovered entities.
     * Default: true
     */
    var autoGenerateRepositories: Boolean = true,
    
    /**
     * Whether to enable query optimization suggestions.
     * Default: true
     */
    var enableQueryOptimization: Boolean = true,
    
    /**
     * Whether to enable debug logging.
     * Default: false
     */
    var debug: Boolean = false,
    
    /**
     * Maximum number of entities to discover in a single scan.
     * Default: 1000
     */
    var maxEntities: Int = 1000,
    
    /**
     * Whether to cache discovered schemas.
     * Default: true
     */
    var cacheSchemas: Boolean = true,
    
    /**
     * Cache TTL in seconds for discovered schemas.
     * Default: 3600 (1 hour)
     */
    var cacheTtl: Long = 3600
) 