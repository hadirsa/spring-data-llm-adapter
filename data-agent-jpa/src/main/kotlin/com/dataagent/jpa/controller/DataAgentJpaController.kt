package com.dataagent.jpa.controller

import com.dataagent.core.model.EntitySchema
import com.dataagent.core.service.DataAgentService
import com.dataagent.jpa.service.JpaSchemaDiscoveryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory

/**
 * JPA-specific REST controller for the Data Agent that provides HTTP endpoints
 * for JPA schema discovery and data access capabilities.
 */
@RestController
@RequestMapping("/api/data-agent/jpa")
class DataAgentJpaController @Autowired constructor(
    private val dataAgentService: DataAgentService,
    private val jpaSchemaDiscoveryService: JpaSchemaDiscoveryService
) {
    
    private val logger = LoggerFactory.getLogger(DataAgentJpaController::class.java)
    
    /**
     * Get all learned JPA entity schemas.
     */
    @GetMapping("/schemas")
    fun getAllJpaSchemas(): ResponseEntity<List<EntitySchema>> {
        logger.debug("Getting all learned JPA schemas")
        val schemas = dataAgentService.getAllLearnedSchemas()
        return ResponseEntity.ok(schemas)
    }
    
    /**
     * Get a specific JPA entity schema by class name.
     */
    @GetMapping("/schemas/{className}")
    fun getJpaSchema(@PathVariable className: String): ResponseEntity<EntitySchema> {
        logger.debug("Getting JPA schema for entity: $className")
        val schema = dataAgentService.getSchema(className)
        return if (schema != null) {
            ResponseEntity.ok(schema)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    /**
     * Get a specific JPA entity schema by table name.
     */
    @GetMapping("/schemas/table/{tableName}")
    fun getJpaSchemaByTable(@PathVariable tableName: String): ResponseEntity<EntitySchema> {
        logger.debug("Getting JPA schema for table: $tableName")
        val schema = dataAgentService.getSchemaByTable(tableName)
        return if (schema != null) {
            ResponseEntity.ok(schema)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    /**
     * Get a summary of all learned JPA entity schemas.
     */
    @GetMapping("/schemas/summary")
    fun getJpaSchemaSummary(): ResponseEntity<Map<String, String>> {
        logger.debug("Getting JPA schema summary")
        val summary = dataAgentService.getSchemaSummary()
        return ResponseEntity.ok(summary)
    }
    
    /**
     * Get detailed information about a specific JPA entity.
     */
    @GetMapping("/entities/{className}/info")
    fun getJpaEntityInfo(@PathVariable className: String): ResponseEntity<String> {
        logger.debug("Getting JPA entity info for: $className")
        val info = dataAgentService.getEntityInfo(className)
        return if (info != null) {
            ResponseEntity.ok(info)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    /**
     * Get all learned JPA entity names.
     */
    @GetMapping("/entities/names")
    fun getJpaEntityNames(): ResponseEntity<List<String>> {
        logger.debug("Getting all JPA entity names")
        val names = dataAgentService.getLearnedEntityNames()
        return ResponseEntity.ok(names)
    }
    
    /**
     * Get all learned JPA table names.
     */
    @GetMapping("/tables/names")
    fun getJpaTableNames(): ResponseEntity<List<String>> {
        logger.debug("Getting all JPA table names")
        val names = dataAgentService.getLearnedTableNames()
        return ResponseEntity.ok(names)
    }
    
    /**
     * Get the total count of learned JPA schemas.
     */
    @GetMapping("/schemas/count")
    fun getJpaSchemaCount(): ResponseEntity<Int> {
        logger.debug("Getting JPA schema count")
        val count = dataAgentService.getLearnedSchemaCount()
        return ResponseEntity.ok(count)
    }
    
    /**
     * Manually discover and learn JPA entity schemas from a specific package.
     */
    @PostMapping("/discover")
    fun discoverJpaSchemas(@RequestParam packageName: String): ResponseEntity<List<EntitySchema>> {
        logger.info("Manually discovering JPA schemas in package: $packageName")
        try {
            val schemas = dataAgentService.discoverAndLearnSchemas(packageName, jpaSchemaDiscoveryService)
            return ResponseEntity.ok(schemas)
        } catch (e: Exception) {
            logger.error("Failed to discover JPA schemas in package: $packageName", e)
            return ResponseEntity.internalServerError().build()
        }
    }
    
    /**
     * Check if a JPA schema exists for a given entity.
     */
    @GetMapping("/schemas/{className}/exists")
    fun hasJpaSchema(@PathVariable className: String): ResponseEntity<Boolean> {
        logger.debug("Checking if JPA schema exists for entity: $className")
        val exists = dataAgentService.hasLearnedSchema(className)
        return ResponseEntity.ok(exists)
    }
    
    /**
     * Clear all learned JPA schemas.
     */
    @DeleteMapping("/schemas")
    fun clearJpaSchemas(): ResponseEntity<Void> {
        logger.info("Clearing all learned JPA schemas")
        dataAgentService.clearLearnedSchemas()
        return ResponseEntity.ok().build()
    }
    
    /**
     * Health check endpoint for JPA functionality.
     */
    @GetMapping("/health")
    fun jpaHealth(): ResponseEntity<Map<String, Any>> {
        val status = mapOf(
            "status" to "UP",
            "type" to "JPA",
            "schemas" to dataAgentService.getLearnedSchemaCount(),
            "entities" to dataAgentService.getLearnedEntityNames().size,
            "tables" to dataAgentService.getLearnedTableNames().size
        )
        return ResponseEntity.ok(status)
    }
} 