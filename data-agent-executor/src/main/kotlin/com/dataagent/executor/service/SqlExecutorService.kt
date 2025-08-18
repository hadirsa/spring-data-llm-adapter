package com.dataagent.executor.service

import com.dataagent.executor.model.QueryResult

/**
 * Service interface for executing SQL queries
 */
interface SqlExecutorService {
    
    /**
     * Execute a SQL query and return the result
     * @param sql The SQL query to execute
     * @param parameters Optional parameters for the query
     * @return QueryResult containing the execution result
     */
    fun executeQuery(sql: String, parameters: Map<String, Any?>? = null): QueryResult
    
    /**
     * Execute a SQL update (INSERT, UPDATE, DELETE) and return the result
     * @param sql The SQL update statement to execute
     * @param parameters Optional parameters for the statement
     * @return QueryResult containing the execution result
     */
    fun executeUpdate(sql: String, parameters: Map<String, Any?>? = null): QueryResult
    
    /**
     * Check if the service is ready to execute queries
     * @return true if ready, false otherwise
     */
    fun isReady(): Boolean
    
    /**
     * Get information about the connected database
     * @return Database information as a map
     */
    fun getDatabaseInfo(): Map<String, Any>
}
