package com.dataagent.core.startup

import com.dataagent.core.service.DataAgentService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * Handles automatic Data Agent initialization on application startup
 */
@Component
class DataAgentDiscoveryRunner(
    private val dataAgentService: DataAgentService
) {
    
    private val logger = LoggerFactory.getLogger(DataAgentDiscoveryRunner::class.java)
    
    @Value("\${data-agent.auto-discover:true}")
    private lateinit var autoDiscover: String
    
    @Value("\${data-agent.scan-packages:}")
    private lateinit var scanPackages: String
    
    @Value("\${data-agent.startup-delay:0}")
    private lateinit var startupDelay: String
    
    /**
     * Automatically discover schemas when the application is ready
     */
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        if (!autoDiscover.toBoolean()) {
            logger.info("Data Agent auto-discovery is disabled")
            return
        }
        
        if (scanPackages.isBlank()) {
            logger.info("No packages configured for auto-discovery")
            return
        }
        
        // Add startup delay if configured (useful for database initialization)
        val delay = startupDelay.toLongOrNull() ?: 0L
        if (delay > 0) {
            logger.info("Waiting $delay ms before starting auto-discovery...")
            Thread.sleep(delay)
        }
        
        logger.info("Starting Data Agent auto-discovery...")
        
        try {
            val packages = scanPackages.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            logger.info("Will scan packages: ${packages.joinToString(", ")}")
            
            // Note: The actual discovery service will be injected by the specific implementation
            // This is just a placeholder - the real implementation will be in JPA or other modules
            logger.info("Auto-discovery completed. Schemas will be discovered when discovery service is available.")
            
        } catch (e: Exception) {
            logger.error("Auto-discovery failed on startup", e)
            // Don't throw exception - auto-discovery failure shouldn't prevent app startup
        }
    }
}
