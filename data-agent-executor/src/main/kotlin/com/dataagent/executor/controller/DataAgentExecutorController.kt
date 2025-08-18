package com.dataagent.executor.controller

import com.dataagent.executor.model.QueryResult
import com.dataagent.executor.service.NaturalLanguageQueryService
import com.dataagent.executor.service.SqlExecutorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * REST controller for Data Agent Executor functionality
 */
@RestController
@RequestMapping("/api/executor")
class DataAgentExecutorController(
    private val naturalLanguageQueryService: NaturalLanguageQueryService,
    private val sqlExecutorService: SqlExecutorService
) {
    
    /**
     * Process a natural language query
     */
    @PostMapping("/query/natural-language")
    fun processNaturalLanguageQuery(
        @RequestBody request: NaturalLanguageQueryRequest
    ): ResponseEntity<QueryResult> {
        val result = naturalLanguageQueryService.processNaturalLanguageQuery(
            request.query,
            request.databaseType ?: "h2"
        )
        
        return if (result.success) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.badRequest().body(result)
        }
    }
    
    /**
     * Execute a raw SQL query with validation
     */
    @PostMapping("/query/sql")
    fun executeSqlQuery(
        @RequestBody request: SqlQueryRequest
    ): ResponseEntity<QueryResult> {
        val result = naturalLanguageQueryService.executeSqlWithValidation(
            request.sql,
            request.parameters
        )
        
        return if (result.success) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.badRequest().body(result)
        }
    }
    
    /**
     * Check if the executor service is ready
     */
    @GetMapping("/status")
    fun getStatus(): ResponseEntity<Map<String, Any>> {
        val status = mapOf(
            "ready" to sqlExecutorService.isReady(),
            "databaseInfo" to sqlExecutorService.getDatabaseInfo(),
            "timestamp" to Date()
        )
        
        return ResponseEntity.ok(status)
    }
    
    /**
     * Get database information
     */
    @GetMapping("/database/info")
    fun getDatabaseInfo(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(sqlExecutorService.getDatabaseInfo())
    }
}

/**
 * Request for natural language queries
 */
data class NaturalLanguageQueryRequest(
    val query: String,
    val databaseType: String? = null
)

/**
 * Request for SQL queries
 */
data class SqlQueryRequest(
    val sql: String,
    val parameters: Map<String, Any?>? = null
)
