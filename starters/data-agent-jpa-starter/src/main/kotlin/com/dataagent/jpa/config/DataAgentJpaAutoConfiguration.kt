package com.dataagent.jpa.config

import com.dataagent.core.service.DataAgentService
import com.dataagent.core.service.SchemaRegistryService
import com.dataagent.jpa.service.JpaSchemaDiscoveryService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import com.dataagent.core.config.DataAgentAutoConfiguration

@Configuration
@ComponentScan(basePackages = ["com.dataagent.jpa"])
@EnableConfigurationProperties(DataAgentJpaProperties::class)
@Import(DataAgentAutoConfiguration::class)
open class DataAgentJpaAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    open fun schemaRegistryService(): SchemaRegistryService = SchemaRegistryService()

    @Bean
    @ConditionalOnMissingBean
    open fun jpaSchemaDiscoveryService(): JpaSchemaDiscoveryService = JpaSchemaDiscoveryService()

    @Bean
    @ConditionalOnMissingBean
    open fun dataAgentService(schemaRegistryService: SchemaRegistryService): DataAgentService =
        DataAgentService(schemaRegistryService)
}
