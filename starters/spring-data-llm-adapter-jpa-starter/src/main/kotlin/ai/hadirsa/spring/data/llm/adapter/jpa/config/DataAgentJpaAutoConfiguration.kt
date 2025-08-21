package ai.hadirsa.spring.data.llm.adapter.jpa.config

import ai.hadirsa.spring.data.llm.adapter.service.DataAgentService
import ai.hadirsa.spring.data.llm.adapter.service.SchemaRegistryService
import ai.hadirsa.spring.data.llm.adapter.jpa.service.JpaSchemaDiscoveryService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import ai.hadirsa.spring.data.llm.adapter.config.DataAgentAutoConfiguration

@Configuration
@ComponentScan(basePackages = ["ai.hadirsa.spring.data.llm.adapter.jpa"])
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
