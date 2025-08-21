package ai.hadirsa.spring.data.llm.adapter.jpa.startup

import ai.hadirsa.spring.data.llm.adapter.service.DataAgentService
import ai.hadirsa.spring.data.llm.adapter.config.DataAgentConfigurationProcessor
import ai.hadirsa.spring.data.llm.adapter.jpa.service.JpaSchemaDiscoveryService
import ai.hadirsa.spring.data.llm.adapter.jpa.config.DataAgentJpaProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DataAgentJpaStartupRunner(
    private val dataAgentService: DataAgentService,
    private val jpaSchemaDiscoveryService: JpaSchemaDiscoveryService,
    private val dataAgentConfigurationProcessor: DataAgentConfigurationProcessor,
    private val properties: DataAgentJpaProperties
) {
    private val logger = LoggerFactory.getLogger(DataAgentJpaStartupRunner::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        logger.info("Spring Data LLM Adapter JPA Startup Runner triggered")
        
        if (!properties.autoDiscover) {
            logger.info("Auto-discovery is disabled. Skipping schema discovery.")
            return
        }
        
        val packagesToScan = getPackagesToScan()
        if (packagesToScan.isEmpty()) {
            logger.warn("No packages specified for scanning. Skipping schema discovery.")
            return
        }
        
        val delay = properties.startupDelay
        logger.info("Waiting ${delay}ms before starting schema discovery...")
        Thread.sleep(delay)
        
        try {
            packagesToScan.forEach { pkg ->
                logger.info("Scanning package for entities: $pkg")
                val discoveredSchemas = dataAgentService.discoverAndLearnSchemas(pkg, jpaSchemaDiscoveryService)
                logger.info("Discovered ${discoveredSchemas.size} schemas from package: $pkg")
            }
            
            val totalSchemas = dataAgentService.getLearnedSchemaCount()
            logger.info("Schema discovery completed. Total schemas learned: $totalSchemas")
        } catch (e: Exception) {
            logger.error("Failed to discover schemas", e)
        }
    }
    
    /**
     * Gets packages to scan, prioritizing configuration properties over annotation-based discovery.
     */
    private fun getPackagesToScan(): List<String> {
        // First, try to use configuration properties
        if (properties.scanPackages.isNotEmpty()) {
            val configPackages = properties.scanPackages
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            
            if (configPackages.isNotEmpty()) {
                logger.info("Using packages from configuration properties: ${configPackages.joinToString(", ")}")
                return configPackages
            }
        }
        
        // Fallback to annotation-based discovery
        try {
            logger.debug("Attempting to discover packages from @EnableDataAgent annotation...")
            val annotationPackages = dataAgentConfigurationProcessor.discoverPackagesToScan()
            if (annotationPackages.isNotEmpty()) {
                logger.info("Using packages from @EnableDataAgent annotation: ${annotationPackages.joinToString(", ")}")
                return annotationPackages
            } else {
                logger.debug("No packages found in @EnableDataAgent annotation")
            }
        } catch (e: Exception) {
            logger.warn("Failed to discover packages from @EnableDataAgent annotation", e)
        }
        
        // If no packages found, try to infer from the main application class
        val inferredPackages = inferPackagesFromMainClass()
        if (inferredPackages.isNotEmpty()) {
            logger.info("Using inferred packages from main class: ${inferredPackages.joinToString(", ")}")
            return inferredPackages
        }
        
        logger.warn("No packages specified for scanning. Please configure 'spring-data-llm-adapter.jpa.scan-packages' or use @EnableDataAgent(packages = \"...\")")
        return emptyList()
    }
    
    /**
     * Infers packages to scan from the main application class.
     */
    private fun inferPackagesFromMainClass(): List<String> {
        return try {
            val stackTrace = Thread.currentThread().stackTrace
            val mainClass = stackTrace.find { 
                it.className.endsWith("Application") || 
                it.className.endsWith("ApplicationKt") ||
                it.className.contains("main")
            }
            
            mainClass?.let { 
                val className = it.className
                val packageName = className.substringBeforeLast(".")
                if (packageName.isNotEmpty()) {
                    // Infer domain package from main application class
                    val domainPackage = "$packageName.domain"
                    logger.debug("Inferred packages: $packageName, $domainPackage")
                    listOf(packageName, domainPackage)
                } else {
                    emptyList()
                }
            } ?: emptyList()
        } catch (e: Exception) {
            logger.debug("Could not infer packages from main class", e)
            emptyList()
        }
    }
}
