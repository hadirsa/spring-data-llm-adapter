package ai.hadirsa.spring.data.llm.adapter.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ai.hadirsa.spring.data.llm.adapter.service.DataAgentService
import ai.hadirsa.spring.data.llm.adapter.service.SchemaRegistryService

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
