package com.dataagent.executor.service

import com.dataagent.core.model.EntitySchema
import com.dataagent.core.service.DataAgentService
import com.dataagent.executor.model.QueryResult
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * Main service for processing natural language queries end-to-end
 */
@Service
class NaturalLanguageQueryService(
    private val dataAgentService: DataAgentService,
    private val llmIntegrationService: LlmIntegrationService,
    private val sqlExecutorService: SqlExecutorService
) {
    
    /**
     * Process a natural language query and return results
     * @param naturalLanguageQuery The natural language query
     * @param databaseType Type of database
     * @return QueryResult with the executed query results
     */
    fun processNaturalLanguageQuery(
        naturalLanguageQuery: String, 
        databaseType: String = "h2"
    ): QueryResult {
        val startTime = System.currentTimeMillis()
        
        try {
            // Get available entity schemas
            val entitySchemas = dataAgentService.getAllLearnedSchemas()
            
            if (entitySchemas.isEmpty()) {
                return QueryResult(
                    success = false,
                    data = emptyList(),
                    rowCount = 0,
                    executionTimeMs = System.currentTimeMillis() - startTime,
                    executedAt = LocalDateTime.now(),
                    errorMessage = "No entity schemas available. Please ensure Data Agent has discovered entities."
                )
            }
            
            // Convert natural language to SQL
            val generatedSql = llmIntegrationService.naturalLanguageToSql(
                naturalLanguageQuery, 
                entitySchemas, 
                databaseType
            )
            
            // Validate SQL safety
            val validationResult = llmIntegrationService.validateSqlSafety(generatedSql, entitySchemas)
            
            if (!validationResult.isValid || validationResult.blocked) {
                return QueryResult(
                    success = false,
                    data = emptyList(),
                    rowCount = 0,
                    executionTimeMs = System.currentTimeMillis() - startTime,
                    executedAt = LocalDateTime.now(),
                    errorMessage = "Generated SQL query is not safe to execute: ${validationResult.reason ?: "Validation failed"}"
                )
            }
            
            // Execute the SQL query
            val queryResult = sqlExecutorService.executeQuery(generatedSql)
            
            // Add metadata about the natural language processing
            val enhancedMetadata = queryResult.metadata?.copy(
                sql = generatedSql,
                parameters = mapOf("naturalLanguageQuery" to naturalLanguageQuery)
            )
            
            return queryResult.copy(
                metadata = enhancedMetadata
            )
            
        } catch (e: Exception) {
            return QueryResult(
                success = false,
                data = emptyList(),
                rowCount = 0,
                executionTimeMs = System.currentTimeMillis() - startTime,
                executedAt = LocalDateTime.now(),
                errorMessage = "Error processing natural language query: ${e.message}"
            )
        }
    }
    
    /**
     * Execute a raw SQL query with safety validation
     * @param sql The SQL query to execute
     * @param parameters Optional parameters
     * @return QueryResult with the execution results
     */
    fun executeSqlWithValidation(
        sql: String, 
        parameters: Map<String, Any?>? = null
    ): QueryResult {
        val startTime = System.currentTimeMillis()
        
        try {
            val entitySchemas = dataAgentService.getAllLearnedSchemas()
            val validationResult = llmIntegrationService.validateSqlSafety(sql, entitySchemas)
            
            if (!validationResult.isValid || validationResult.blocked) {
                return QueryResult(
                    success = false,
                    data = emptyList(),
                    rowCount = 0,
                    executionTimeMs = System.currentTimeMillis() - startTime,
                    executedAt = LocalDateTime.now(),
                    errorMessage = "SQL query is not safe to execute: ${validationResult.reason ?: "Validation failed"}"
                )
            }
            
            return sqlExecutorService.executeQuery(sql, parameters)
            
        } catch (e: Exception) {
            return QueryResult(
                success = false,
                data = emptyList(),
                rowCount = 0,
                executionTimeMs = System.currentTimeMillis() - startTime,
                executedAt = LocalDateTime.now(),
                errorMessage = "Error executing SQL query: ${e.message}"
            )
        }
    }
}
