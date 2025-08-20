package com.dataagent.examples.jpa.domain

import com.dataagent.core.annotation.DataAgent
import com.dataagent.core.annotation.DataAgentField
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime

@DataAgent(
    description = "Example JPA entity User for demonstration purposes",
    discoverable = true
)
@Entity
@Table(
    name = "UM_USER",
    uniqueConstraints = [UniqueConstraint(name = "UM_USERNAME_UK", columnNames = ["username"])]
)
class User(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    @DataAgentField(
        description = "Unique identifier for the user",
        category = "identification",
        examples = "uuid-string"
    )
    val id: String? = null,

    @field:Size(max = 50)
    @Column(name = "first_name", length = 50)
    @DataAgentField(
        description = "User's first name",
        category = "personal_info",
        sensitive = true,
        examples = "John, Jane"
    )
    val firstName: String? = null,

    @field:Size(max = 50)
    @Column(name = "last_name", length = 50)
    @DataAgentField(
        description = "User's last name",
        category = "personal_info",
        sensitive = true,
        examples = "Doe, Smith"
    )
    val lastName: String? = null,

    @field:NotNull
    @field:Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    @DataAgentField(
        description = "Unique username for login",
        category = "authentication",
        examples = "john.doe, jane.smith"
    )
    val username: String,

    @field:Size(min = 8, max = 100)
    @Column(length = 100)
    @DataAgentField(
        description = "User's password (hashed)",
        category = "authentication",
        sensitive = true,
        dataQuality = "Must be at least 8 characters"
    )
    val password: String? = null,

    @field:Email
    @field:Size(min = 5, max = 254)
    @Column(length = 254)
    @DataAgentField(
        description = "User's email address",
        category = "contact",
        sensitive = true,
        examples = "john.doe@example.com"
    )
    val email: String? = null,

    @Column(name = "is_online", nullable = false)
    @DataAgentField(
        description = "Whether the user is currently online",
        category = "status",
        examples = "true, false"
    )
    val online: Boolean = false,

    @field:NotNull
    @Column(name = "is_activated", nullable = false)
    @DataAgentField(
        description = "Whether the user account is activated",
        category = "status",
        examples = "true, false"
    )
    val activated: Boolean = false,

    @DataAgentField(
        description = "Timestamp of user's last login",
        category = "audit",
        examples = "2024-01-15T10:30:00"
    )
    val lastLogin: LocalDateTime? = null,

    @DataAgentField(
        description = "Number of failed login attempts",
        category = "security",
        examples = "0, 1, 2"
    )
    val wrongTries: Int? = null,

    @DataAgentField(
        description = "Whether user must change password on next login",
        category = "security",
        examples = "true, false"
    )
    val forceToChangePassword: Boolean = false,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "profile_id")
    @DataAgentField(
        description = "Associated user profile information",
        category = "relationship",
        examples = "Profile entity reference"
    )
    val profile: Profile? = null
)
