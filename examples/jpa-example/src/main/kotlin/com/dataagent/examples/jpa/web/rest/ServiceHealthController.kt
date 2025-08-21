package ai.hadirsa.examples.jpa.web.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Health and readiness checks")
class ServiceHealthController {

    @GetMapping
    @Operation(summary = "Basic health check")
    fun health(): Map<String, Any> = mapOf(
        "status" to "UP",
        "timestamp" to System.currentTimeMillis()
    )
}
