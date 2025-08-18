package com.dataagent.examples.jpa

import com.dataagent.core.service.DataAgentService
import com.dataagent.jpa.service.JpaSchemaDiscoveryService
import org.springframework.web.bind.annotation.*

/**
 * Controller to test automatic schema discovery
 */
@RestController
@RequestMapping("/api/data-agent/discovery")
class AutoDiscoveryTestController(
    private val dataAgentService: DataAgentService,
    private val jpaSchemaDiscoveryService: JpaSchemaDiscoveryService
) {

    @GetMapping("/health")
    fun getHealth(): Map<String, Any> {
        val schemaCount = dataAgentService.getLearnedSchemaCount()
        return mapOf(
            "status" to "UP",
            "autoDiscoveryEnabled" to true,
            "schemasDiscovered" to (schemaCount > 0),
            "timestamp" to System.currentTimeMillis()
        )
    }

    @GetMapping("/schemas")
    fun getDiscoveredSchemas(): Map<String, Any> {
        val schemas = dataAgentService.getAllLearnedSchemas()
        val summary = dataAgentService.getSchemaSummary()
        
        return mapOf(
            "totalSchemas" to schemas.size,
            "schemas" to schemas.map { schema ->
                mapOf(
                    "entityName" to schema.entityClassName,
                    "tableName" to schema.tableName,
                    "description" to schema.description,
                    "fieldCount" to schema.fields.size,
                    "relationshipCount" to schema.relationships.size,
                    "discoveredAt" to schema.discoveredAt
                )
            },
            "summary" to summary
        )
    }
    
    @GetMapping("/schemas/count")
    fun getSchemaCount(): Map<String, Int> {
        return mapOf(
            "totalSchemas" to dataAgentService.getLearnedSchemaCount()
        )
    }

    @GetMapping("/schema/{entityName}")
    fun getSchemaDetails(@PathVariable entityName: String): Map<String, Any> {
        return try {
            val schema = dataAgentService.getSchema(entityName)
            if (schema != null) {
                mapOf(
                    "success" to true,
                    "schema" to mapOf(
                        "entityClassName" to schema.entityClassName,
                        "tableName" to schema.tableName,
                        "description" to schema.description,
                        "discoveredAt" to schema.discoveredAt.toString(),
                        "fields" to schema.fields.map { field ->
                            mapOf(
                                "fieldName" to field.fieldName,
                                "columnName" to field.columnName,
                                "fieldType" to field.fieldType,
                                "columnType" to field.columnType,
                                "nullable" to field.nullable,
                                "isPrimaryKey" to field.isPrimaryKey,
                                "unique" to field.unique,
                                "length" to field.length
                            )
                        },
                        "relationships" to schema.relationships.map { rel ->
                            mapOf(
                                "fieldName" to rel.fieldName,
                                "targetEntity" to rel.targetEntity,
                                "relationshipType" to rel.relationshipType,
                                "mappedBy" to rel.mappedBy,
                                "joinColumn" to rel.joinColumn,
                                "fetchType" to rel.fetchType
                            )
                        }
                    )
                )
            } else {
                mapOf(
                    "success" to false,
                    "error" to "Schema not found for entity: $entityName"
                )
            }
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "Unknown error")
            )
        }
    }

    @PostMapping("/schema/learn")
    fun learnSchemas(): Map<String, Any> {
        return try {
            dataAgentService.discoverAndLearnSchemas("com.dataagent.examples.jpa", jpaSchemaDiscoveryService)
            val schemas = dataAgentService.getAllLearnedSchemas()
            mapOf(
                "success" to true,
                "message" to "Schemas learned successfully",
                "schemasCount" to schemas.size,
                "schemas" to schemas.map {
                    mapOf(
                        "entityClassName" to it.entityClassName,
                        "tableName" to it.tableName,
                        "description" to it.description,
                        "fieldsCount" to it.fields.size,
                        "relationshipsCount" to it.relationships.size
                    )
                }
            )
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "Unknown error")
            )
        }
    }
} 
