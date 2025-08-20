package com.dataagent.core.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.dataagent.core.service.DataAgentService
import com.dataagent.core.service.SchemaDiscoveryService
import com.dataagent.core.service.SchemaRegistryService

/**
 * Core auto-configuration for Data Agent functionality.
 * This configuration provides the essential beans needed for Data Agent operations
 * across all supported modules.
 */
@Configuration
open class DataAgentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    open fun dataAgentService(schemaRegistryService: SchemaRegistryService): DataAgentService {
        return DataAgentService(schemaRegistryService)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun schemaRegistryService(): SchemaRegistryService {
        return SchemaRegistryService()
    }
    
    @Bean
    @ConditionalOnMissingBean
    open fun dataAgentConfigurationProcessor(): DataAgentConfigurationProcessor {
        return DataAgentConfigurationProcessor()
    }
}
