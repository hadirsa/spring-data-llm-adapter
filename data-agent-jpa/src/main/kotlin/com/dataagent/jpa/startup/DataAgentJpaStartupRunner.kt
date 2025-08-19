package com.dataagent.jpa.startup

import com.dataagent.core.service.DataAgentService
import com.dataagent.jpa.service.JpaSchemaDiscoveryService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * Handles automatic JPA schema discovery on application startup
 */
@Component
class DataAgentJpaStartupRunner(
    private val dataAgentService: DataAgentService,
    private val jpaSchemaDiscoveryService: JpaSchemaDiscoveryService
) {
    
    private val logger = LoggerFactory.getLogger(DataAgentJpaStartupRunner::class.java)
    
    @Value("\${data-agent.jpa.auto-discover:true}")
    private lateinit var autoDiscover: String
    
    @Value("\${data-agent.jpa.scan-packages:}")
    private lateinit var scanPackages: String
    
    @Value("\${data-agent.jpa.startup-delay:1000}")
    private lateinit var startupDelay: String
    
    /**
     * Automatically discover JPA schemas when the application is ready
     */
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        if (!autoDiscover.toBoolean()) {
            logger.info("Data Agent JPA auto-discovery is disabled")
            return
        }
        
        if (scanPackages.isBlank()) {
            logger.info("No packages configured for JPA auto-discovery")
            return
        }
        
        // Add startup delay to ensure database and JPA are fully initialized
        val delay = startupDelay.toLongOrNull() ?: 1000L
        logger.info("Waiting $delay ms for JPA initialization before starting auto-discovery...")
        Thread.sleep(delay)
        
        logger.info("Starting Data Agent JPA auto-discovery on application startup...")
        
        try {
            val packages = scanPackages.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            logger.info("Will scan packages for JPA entities: ${packages.joinToString(", ")}")
            
            var totalSchemas = 0
            packages.forEach { packageName ->
                try {
                    logger.info("Discovering JPA schemas in package: $packageName")
                    val schemas = dataAgentService.discoverAndLearnSchemas(packageName, jpaSchemaDiscoveryService)
                    totalSchemas += schemas.size
                    logger.info("Discovered ${schemas.size} JPA schemas in package: $packageName")
                } catch (e: Exception) {
                    logger.error("Failed to discover schemas in package: $packageName", e)
                    // Continue with other packages
                }
            }
            
            logger.info("JPA auto-discovery completed. Total schemas discovered: $totalSchemas")
            
        } catch (e: Exception) {
            logger.error("JPA auto-discovery failed on startup", e)
            // Don't throw exception - auto-discovery failure shouldn't prevent app startup
        }
    }
}
