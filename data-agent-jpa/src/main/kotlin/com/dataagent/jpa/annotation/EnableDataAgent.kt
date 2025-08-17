package com.dataagent.jpa.annotation

import org.springframework.context.annotation.Import
import com.dataagent.jpa.config.DataAgentJpaAutoConfiguration

/**
 * Enables the Data Agent JPA functionality in a Spring Boot application.
 * This annotation imports the necessary JPA-specific configuration to activate
 * schema learning and data access capabilities for JPA entities.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(DataAgentJpaAutoConfiguration::class)
annotation class EnableDataAgent(
    /**
     * Whether to enable automatic JPA entity discovery on startup
     */
    val autoDiscover: Boolean = true,
    
    /**
     * Whether to enable automatic repository generation
     */
    val autoGenerateRepositories: Boolean = true,
    
    /**
     * Whether to enable query optimization suggestions
     */
    val enableQueryOptimization: Boolean = true
) 