package com.dataagent.core.annotation

import org.springframework.context.annotation.Import
import com.dataagent.core.config.DataAgentAutoConfiguration

/**
 * Enables the Data Agent functionality in a Spring Boot application.
 * This annotation imports the necessary configuration to activate
 * schema learning and data access capabilities across all supported modules.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(DataAgentAutoConfiguration::class)
annotation class EnableDataAgent(
    /**
     * Whether to enable automatic entity discovery on startup
     */
    val autoDiscover: Boolean = true,
    
    /**
     * Packages to scan for entities. If not specified, will use configuration properties.
     * Multiple packages can be specified as comma-separated values.
     * Example: @EnableDataAgent(packages = "com.example.domain,com.example.entities")
     */
    val packages: String = ""
)
