package com.dataagent.core.config

import com.dataagent.core.annotation.EnableDataAgent
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.annotation.Autowired

/**
 * Configuration processor that extracts package information from @EnableDataAgent annotations.
 * This allows the Data Agent to automatically discover which packages to scan for entities.
 */
@Component
class DataAgentConfigurationProcessor {
    
    private val logger = LoggerFactory.getLogger(DataAgentConfigurationProcessor::class.java)
    
    @Autowired
    private lateinit var applicationContext: ApplicationContext
    
    /**
     * Discovers packages to scan by looking for @EnableDataAgent annotations in the application.
     * Returns a list of packages to scan, or empty list if none found.
     */
    fun discoverPackagesToScan(): List<String> {
        val packages = mutableListOf<String>()
        
        try {
            // First, try to find the main application class from the application context
            val mainClass = findMainApplicationClass()
            if (mainClass != null) {
                val annotation = mainClass.getAnnotation(EnableDataAgent::class.java)
                if (annotation != null && annotation.packages.isNotEmpty()) {
                    val annotationPackages = annotation.packages
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    
                    packages.addAll(annotationPackages)
                    logger.info("Found @EnableDataAgent annotation with packages: ${annotationPackages.joinToString(", ")}")
                    return packages
                }
            }
            
            // Fallback: Scan for classes with @EnableDataAgent annotation
            val scanner = ClassPathScanningCandidateComponentProvider(false)
            scanner.addIncludeFilter(AnnotationTypeFilter(EnableDataAgent::class.java))
            
            // Use a broader base package for scanning
            val basePackage = getBasePackage()
            if (basePackage.isNotEmpty()) {
                logger.debug("Scanning for @EnableDataAgent annotations in base package: $basePackage")
                val candidates = scanner.findCandidateComponents(basePackage)
                
                candidates.forEach { beanDefinition ->
                    try {
                        val clazz = Class.forName(beanDefinition.beanClassName)
                        val annotation = clazz.getAnnotation(EnableDataAgent::class.java)
                        
                        if (annotation != null && annotation.packages.isNotEmpty()) {
                            val annotationPackages = annotation.packages
                                .split(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }
                            
                            packages.addAll(annotationPackages)
                            logger.info("Found @EnableDataAgent annotation with packages: ${annotationPackages.joinToString(", ")}")
                        }
                    } catch (e: Exception) {
                        logger.warn("Failed to process @EnableDataAgent annotation in ${beanDefinition.beanClassName}", e)
                    }
                }
            }
        } catch (e: Exception) {
            logger.warn("Failed to discover @EnableDataAgent annotations", e)
        }
        
        // Remove duplicates and return
        return packages.distinct()
    }
    
    /**
     * Finds the main application class from the application context.
     */
    private fun findMainApplicationClass(): Class<*>? {
        return try {
            // Look for beans that are likely the main application class
            val mainBeans = applicationContext.beanDefinitionNames
                .filter { it.endsWith("Application") || it.endsWith("ApplicationKt") }
                .mapNotNull { beanName ->
                    try {
                        applicationContext.getType(beanName)
                    } catch (e: Exception) {
                        null
                    }
                }
                .filter { it != null }
                .firstOrNull()
            
            mainBeans
        } catch (e: Exception) {
            logger.debug("Could not find main application class from context", e)
            null
        }
    }
    
    /**
     * Gets the base package for scanning. This tries to determine the main application package.
     */
    private fun getBasePackage(): String {
        return try {
            // Try to get the main application class from the application context first
            val mainClass = findMainApplicationClass()
            if (mainClass != null) {
                val packageName = mainClass.`package`.name
                if (packageName.isNotEmpty()) {
                    logger.debug("Using package from main application class: $packageName")
                    return packageName
                }
            }
            
            // Fallback: Try to get from stack trace
            val stackTrace = Thread.currentThread().stackTrace
            val mainClassFromStack = stackTrace.find { 
                it.className.endsWith("Application") || 
                it.className.endsWith("ApplicationKt") ||
                it.className.contains("main")
            }
            
            mainClassFromStack?.let { 
                val className = it.className
                val packageName = className.substringBeforeLast(".")
                if (packageName.isNotEmpty()) {
                    logger.debug("Using package from stack trace: $packageName")
                    return packageName
                }
            }
            
            // Default fallback
            "com.dataagent"
        } catch (e: Exception) {
            logger.debug("Could not determine base package, using default", e)
            "com.dataagent"
        }
    }
    
    /**
     * Alternative method to get packages from a specific class.
     * This is useful when you know the main application class.
     */
    fun getPackagesFromClass(clazz: Class<*>): List<String> {
        val annotation = clazz.getAnnotation(EnableDataAgent::class.java)
        return if (annotation != null && annotation.packages.isNotEmpty()) {
            annotation.packages
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        } else {
            emptyList()
        }
    }
}
