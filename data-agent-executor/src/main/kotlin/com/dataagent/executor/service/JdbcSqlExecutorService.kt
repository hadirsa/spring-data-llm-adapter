package com.dataagent.executor.service

import com.dataagent.executor.model.QueryMetadata
import com.dataagent.executor.model.QueryResult
import com.dataagent.executor.model.QueryType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.time.LocalDateTime
import javax.sql.DataSource

/**
 * JDBC implementation of SqlExecutorService
 */
@Service
class JdbcSqlExecutorService(private val dataSource: DataSource) : SqlExecutorService {
    
    private val jdbcTemplate = JdbcTemplate(dataSource)
    
    override fun executeQuery(sql: String, parameters: Map<String, Any?>?): QueryResult {
        val startTime = System.currentTimeMillis()
        
        return try {
            val results = if (parameters != null && parameters.isNotEmpty()) {
                val paramArray = parameters.values.toTypedArray()
                jdbcTemplate.query(sql, paramArray, ResultSetRowMapper())
            } else {
                jdbcTemplate.query(sql, ResultSetRowMapper())
            }
            
            val executionTime = System.currentTimeMillis() - startTime
            
            QueryResult(
                success = true,
                data = results,
                rowCount = results.size,
                executionTimeMs = executionTime,
                executedAt = LocalDateTime.now(),
                metadata = QueryMetadata(
                    sql = sql,
                    parameters = parameters,
                    queryType = QueryType.SELECT
                )
            )
            
        } catch (e: Exception) {
            QueryResult(
                success = false,
                data = emptyList(),
                rowCount = 0,
                executionTimeMs = System.currentTimeMillis() - startTime,
                executedAt = LocalDateTime.now(),
                errorMessage = "Error executing query: ${e.message}"
            )
        }
    }
    
    override fun executeUpdate(sql: String, parameters: Map<String, Any?>?): QueryResult {
        val startTime = System.currentTimeMillis()
        
        return try {
            val affectedRows = if (parameters != null && parameters.isNotEmpty()) {
                val paramArray = parameters.values.toTypedArray()
                jdbcTemplate.update(sql, *paramArray)
            } else {
                jdbcTemplate.update(sql)
            }
            
            val executionTime = System.currentTimeMillis() - startTime
            val queryType = determineQueryType(sql)
            
            QueryResult(
                success = true,
                data = emptyList(),
                rowCount = affectedRows,
                executionTimeMs = executionTime,
                executedAt = LocalDateTime.now(),
                metadata = QueryMetadata(
                    sql = sql,
                    parameters = parameters,
                    queryType = queryType,
                    affectedRows = affectedRows
                )
            )
            
        } catch (e: Exception) {
            QueryResult(
                success = false,
                data = emptyList(),
                rowCount = 0,
                executionTimeMs = System.currentTimeMillis() - startTime,
                executedAt = LocalDateTime.now(),
                errorMessage = "Error executing update: ${e.message}"
            )
        }
    }
    
    override fun isReady(): Boolean {
        return try {
            jdbcTemplate.queryForObject("SELECT 1", Int::class.java) == 1
        } catch (e: Exception) {
            false
        }
    }
    
    override fun getDatabaseInfo(): Map<String, Any> {
        return try {
            val databaseProductName = jdbcTemplate.queryForObject(
                "SELECT ?", String::class.java, "SELECT version()"
            ) ?: "Unknown"
            
            val databaseProductVersion = jdbcTemplate.queryForObject(
                "SELECT ?", String::class.java, "SELECT version()"
            ) ?: "Unknown"
            
            mapOf(
                "productName" to databaseProductName,
                "productVersion" to databaseProductVersion,
                "url" to (dataSource.connection?.metaData?.url ?: "Unknown"),
                "driverName" to (dataSource.connection?.metaData?.driverName ?: "Unknown"),
                "driverVersion" to (dataSource.connection?.metaData?.driverVersion ?: "Unknown")
            )
        } catch (e: Exception) {
            mapOf(
                "error" to "Unable to retrieve database info: ${e.message}",
                "ready" to isReady()
            )
        }
    }
    
    private fun determineQueryType(sql: String): QueryType {
        val upperSql = sql.trim().uppercase()
        return when {
            upperSql.startsWith("INSERT") -> QueryType.INSERT
            upperSql.startsWith("UPDATE") -> QueryType.UPDATE
            upperSql.startsWith("DELETE") -> QueryType.DELETE
            upperSql.startsWith("CREATE") || upperSql.startsWith("ALTER") || 
            upperSql.startsWith("DROP") || upperSql.startsWith("TRUNCATE") -> QueryType.DDL
            else -> QueryType.OTHER
        }
    }
    
    /**
     * Row mapper that converts ResultSet to Map<String, Any?>
     */
    private class ResultSetRowMapper : RowMapper<Map<String, Any?>> {
        override fun mapRow(rs: ResultSet, rowNum: Int): Map<String, Any?> {
            val metaData: ResultSetMetaData = rs.metaData
            val columnCount = metaData.columnCount
            val row = mutableMapOf<String, Any?>()
            
            for (i in 1..columnCount) {
                val columnName = metaData.getColumnLabel(i)
                val value = rs.getObject(i)
                row[columnName] = value
            }
            
            return row
        }
    }
}
