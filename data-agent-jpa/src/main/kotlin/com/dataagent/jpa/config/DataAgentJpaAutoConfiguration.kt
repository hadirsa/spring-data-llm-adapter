package com.dataagent.jpa.config

import com.dataagent.core.service.DataAgentService
import com.dataagent.core.service.SchemaRegistryService
import com.dataagent.jpa.service.JpaSchemaDiscoveryService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ComponentScan

/**
 * JPA-specific auto-configuration for the Data Agent.
 * This class provides JPA-specific beans and configuration.
 */
@Configuration
@ComponentScan(basePackages = ["com.dataagent.jpa"])
@EnableConfigurationProperties(DataAgentProperties::class)
open class DataAgentJpaAutoConfiguration {
    
    /**
     * Creates the SchemaRegistryService bean if it doesn't exist.
     */
    @Bean
    @ConditionalOnMissingBean
    open fun schemaRegistryService(): SchemaRegistryService {
        return SchemaRegistryService()
    }
    
    /**
     * Creates the JPA-specific SchemaDiscoveryService bean if it doesn't exist.
     */
    @Bean
    @ConditionalOnMissingBean
    open fun jpaSchemaDiscoveryService(): JpaSchemaDiscoveryService {
        return JpaSchemaDiscoveryService()
    }
    
    /**
     * Creates the DataAgentService bean.
     */
    @Bean
    @ConditionalOnMissingBean
    open fun dataAgentService(
        schemaRegistryService: SchemaRegistryService
    ): DataAgentService {
        return DataAgentService(schemaRegistryService)
    }
} 
