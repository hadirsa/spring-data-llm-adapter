package com.dataagent.executor.config

import com.dataagent.executor.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

/**
 * Auto-configuration for Data Agent Executor module
 */
@Configuration
@ConditionalOnClass(JdbcTemplate::class)
@ConditionalOnProperty(prefix = "data-agent.executor", name = ["enabled"], havingValue = "true", matchIfMissing = true)
open class DataAgentExecutorAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    open fun sqlExecutorService(dataSource: DataSource): SqlExecutorService {
        return JdbcSqlExecutorService(dataSource)
    }
    
    @Bean
    @ConditionalOnMissingBean
    open fun llmIntegrationService(): LlmIntegrationService {
        return MockLlmIntegrationService()
    }
    
    @Bean
    @ConditionalOnMissingBean
    open fun naturalLanguageQueryService(
        sqlExecutorService: SqlExecutorService,
        llmIntegrationService: LlmIntegrationService
    ): NaturalLanguageQueryService {
        // Note: DataAgentService will be injected by the application
        return NaturalLanguageQueryService(
            dataAgentService = null!!, // Will be injected by Spring
            llmIntegrationService = llmIntegrationService,
            sqlExecutorService = sqlExecutorService
        )
    }
}
