package com.dataagent.examples.jpa

import com.dataagent.jpa.annotation.DataAgent
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
    val id: Long? = null,
    
    @Column(name = "name", length = 100, nullable = false)
    val name: String,
    
    @Column(name = "description", length = 500)
    val description: String? = null,
    
    @Column(name = "active", nullable = false)
    val active: Boolean = true,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
)
