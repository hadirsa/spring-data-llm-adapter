package ai.hadirsa.examples.jpa.web.rest

import ai.hadirsa.spring.data.llm.adapter.service.DataAgentService
import ai.hadirsa.spring.data.llm.adapter.jpa.service.JpaSchemaDiscoveryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/schemas")
@Tag(name = "Schemas", description = "RESTful endpoints for Data Agent schemas")
class SchemaController(
    private val dataAgentService: DataAgentService,
    private val jpaSchemaDiscoveryService: JpaSchemaDiscoveryService
) {

    @GetMapping
    @Operation(summary = "List learned schemas")
    fun listSchemas(): ResponseEntity<Any> = ResponseEntity.ok(
        dataAgentService.getAllLearnedSchemas().map {
            mapOf(
                "entityClassName" to it.entityClassName,
                "tableName" to it.tableName,
                "fieldCount" to it.fields.size,
                "relationshipCount" to it.relationships.size
            )
        }
    )

    @GetMapping("/{entityName}")
    @Operation(summary = "Get schema details by entity name")
    fun getSchema(@PathVariable entityName: String): ResponseEntity<Any> {
        val schema = dataAgentService.getSchema(entityName)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            mapOf(
                "entityClassName" to schema.entityClassName,
                "tableName" to schema.tableName,
                "description" to schema.description,
                "fields" to schema.fields,
                "relationships" to schema.relationships
            )
        )
    }

    @PostMapping("/discover")
    @Operation(summary = "Trigger discovery and learning")
    fun discover(): ResponseEntity<Any> {
        dataAgentService.discoverAndLearnSchemas("ai.hadirsa.examples.jpa", jpaSchemaDiscoveryService)
        return ResponseEntity.accepted().body(mapOf("message" to "Discovery triggered"))
    }
}
