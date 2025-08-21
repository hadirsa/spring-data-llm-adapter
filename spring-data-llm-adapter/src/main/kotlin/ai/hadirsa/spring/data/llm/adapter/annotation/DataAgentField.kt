package ai.hadirsa.spring.data.llm.adapter.annotation

import kotlin.annotation.AnnotationTarget.FIELD

/**
 * Annotation to provide additional metadata for Data Agent field discovery.
 * This annotation can be used to enhance schema generation with field-level descriptions
 * and metadata across all Data Agent modules.
 */
@Target(FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class DataAgentField(
    /**
     * Human-readable description of the field
     */
    val description: String = "",
    
    /**
     * Business category or domain this field belongs to
     */
    val category: String = "",
    
    /**
     * Whether this field is sensitive/personal information
     */
    val sensitive: Boolean = false,
    
    /**
     * Data quality rules or constraints for this field
     */
    val dataQuality: String = "",
    
    /**
     * Example values for this field
     */
    val examples: String = ""
)
