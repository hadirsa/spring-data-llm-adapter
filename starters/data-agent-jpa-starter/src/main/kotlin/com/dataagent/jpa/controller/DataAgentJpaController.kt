package com.dataagent.jpa.controller

import com.dataagent.core.model.EntitySchema
import com.dataagent.core.service.DataAgentService
import com.dataagent.jpa.service.JpaSchemaDiscoveryService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/data-agent/jpa")
class DataAgentJpaController(
    private val dataAgentService: DataAgentService,
    private val jpaSchemaDiscoveryService: JpaSchemaDiscoveryService
) {
    private val logger = LoggerFactory.getLogger(DataAgentJpaController::class.java)

    @GetMapping("/schemas")
    fun getAllJpaSchemas(): ResponseEntity<List<EntitySchema>> = ResponseEntity.ok(dataAgentService.getAllLearnedSchemas())

    @GetMapping("/schemas/{className}")
    fun getJpaSchema(@PathVariable className: String): ResponseEntity<EntitySchema> =
        dataAgentService.getSchema(className)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

    @GetMapping("/schemas/table/{tableName}")
    fun getJpaSchemaByTable(@PathVariable tableName: String): ResponseEntity<EntitySchema> =
        dataAgentService.getSchemaByTable(tableName)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

    @GetMapping("/schemas/summary")
    fun getJpaSchemaSummary(): ResponseEntity<Map<String, String>> = ResponseEntity.ok(dataAgentService.getSchemaSummary())

    @GetMapping("/entities/{className}/info")
    fun getJpaEntityInfo(@PathVariable className: String): ResponseEntity<String> =
        dataAgentService.getEntityInfo(className)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

    @GetMapping("/entities/names")
    fun getJpaEntityNames(): ResponseEntity<List<String>> = ResponseEntity.ok(dataAgentService.getLearnedEntityNames())

    @GetMapping("/tables/names")
    fun getJpaTableNames(): ResponseEntity<List<String>> = ResponseEntity.ok(dataAgentService.getLearnedTableNames())

    @GetMapping("/schemas/count")
    fun getJpaSchemaCount(): ResponseEntity<Int> = ResponseEntity.ok(dataAgentService.getLearnedSchemaCount())

    @PostMapping("/discover")
    fun discoverJpaSchemas(@RequestParam packageName: String): ResponseEntity<List<EntitySchema>> = try {
        ResponseEntity.ok(dataAgentService.discoverAndLearnSchemas(packageName, jpaSchemaDiscoveryService))
    } catch (e: Exception) {
        ResponseEntity.internalServerError().build()
    }

    @GetMapping("/schemas/{className}/exists")
    fun hasJpaSchema(@PathVariable className: String): ResponseEntity<Boolean> = ResponseEntity.ok(dataAgentService.hasLearnedSchema(className))

    @DeleteMapping("/schemas")
    fun clearJpaSchemas(): ResponseEntity<Void> { dataAgentService.clearLearnedSchemas(); return ResponseEntity.ok().build() }

    @GetMapping("/health")
    fun jpaHealth(): ResponseEntity<Map<String, Any>> = ResponseEntity.ok(
        mapOf(
            "status" to "UP",
            "type" to "JPA",
            "schemas" to dataAgentService.getLearnedSchemaCount(),
            "entities" to dataAgentService.getLearnedEntityNames().size,
            "tables" to dataAgentService.getLearnedTableNames().size
        )
    )
}
