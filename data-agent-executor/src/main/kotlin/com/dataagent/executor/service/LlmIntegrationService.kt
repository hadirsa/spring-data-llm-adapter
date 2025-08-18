package com.dataagent.executor.service

import com.dataagent.core.model.EntitySchema

/**
 * Service interface for LLM integration to convert natural language to SQL
 */
interface LlmIntegrationService {
    
    /**
     * Convert natural language query to SQL using LLM
     * @param naturalLanguageQuery The natural language query
     * @param entitySchemas Available entity schemas for context
     * @param databaseType Type of database (e.g., "postgresql", "mysql", "h2")
     * @return Generated SQL query
     */
    fun naturalLanguageToSql(
        naturalLanguageQuery: String, 
        entitySchemas: List<EntitySchema>, 
        databaseType: String
    ): String
    
    /**
     * Validate if a generated SQL query is safe to execute
     * @param sql The SQL query to validate
     * @param entitySchemas Available entity schemas for validation
     * @return Validation result with safety score and warnings
     */
    fun validateSqlSafety(sql: String, entitySchemas: List<EntitySchema>): SqlValidationResult
    
    /**
     * Check if the LLM service is available
     * @return true if available, false otherwise
     */
    fun isAvailable(): Boolean
}

/**
 * Result of SQL validation
 */
data class SqlValidationResult(
    val isValid: Boolean,
    val safetyScore: Int, // 0-100
    val warnings: List<String>,
    val blocked: Boolean,
    val reason: String? = null
)
