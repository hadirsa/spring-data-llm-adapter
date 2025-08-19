package com.dataagent.examples.jpa.domain

import com.dataagent.jpa.annotation.DataAgent
import com.dataagent.core.annotation.DataAgentField
import jakarta.persistence.*

/**
 * Example JPA entity demonstrating the @DataAgent annotation.
 * This entity will be automatically discovered and learned by the Data Agent.
 */
@DataAgent(
    description = "Example JPA entity for demonstration purposes",
    discoverable = true
)
@Entity
@Table(name = "example_jpa_entities")
class ExampleJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DataAgentField(
        description = "Primary key identifier",
        category = "identification",
        examples = "1, 2, 3"
    )
    val id: Long? = null,
    
    @Column(name = "name", length = 100, nullable = false)
    @DataAgentField(
        description = "Name of the example entity",
        category = "basic_info",
        examples = "Sample Entity, Test Item"
    )
    val name: String,
    
    @Column(name = "description", length = 500)
    @DataAgentField(
        description = "Detailed description of the entity",
        category = "documentation",
        examples = "This is a sample entity for demonstration"
    )
    val description: String? = null,
    
    @Column(name = "active", nullable = false)
    @DataAgentField(
        description = "Whether the entity is currently active",
        category = "status",
        examples = "true, false"
    )
    val active: Boolean = true,
    
    @Column(name = "created_at", nullable = false)
    @DataAgentField(
        description = "Timestamp when the entity was created",
        category = "audit",
        examples = "2024-01-15T10:30:00"
    )
    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
)
