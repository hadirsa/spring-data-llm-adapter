package com.dataagent.executor.model

import java.time.LocalDateTime

/**
 * Represents the result of a SQL query execution
 */
data class QueryResult(
    val success: Boolean,
    val data: List<Map<String, Any?>>,
    val rowCount: Int,
    val executionTimeMs: Long,
    val executedAt: LocalDateTime,
    val errorMessage: String? = null,
    val metadata: QueryMetadata? = null
)

/**
 * Metadata about the executed query
 */
data class QueryMetadata(
    val sql: String,
    val parameters: Map<String, Any?>? = null,
    val queryType: QueryType,
    val affectedRows: Int? = null,
    val generatedKeys: List<Any>? = null
)

/**
 * Type of SQL query executed
 */
enum class QueryType {
    SELECT, INSERT, UPDATE, DELETE, DDL, OTHER
}
