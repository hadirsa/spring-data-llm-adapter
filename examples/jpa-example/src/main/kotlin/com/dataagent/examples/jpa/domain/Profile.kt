package ai.hadirsa.examples.jpa.domain

import ai.hadirsa.spring.data.llm.adapter.annotation.DataAgentField
import ai.hadirsa.spring.data.llm.adapter.annotation.DataAgent
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator

@DataAgent(
    description = "Example JPA entity Profile for demonstration purposes",
    discoverable = true
)
@Entity
@Table(name = "UM_PROFILE")
class Profile(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    @DataAgentField(
        description = "Unique identifier for the profile",
        category = "identification",
        examples = "uuid-string"
    )
    val id: String? = null,
    
    @DataAgentField(
        description = "National identification number",
        category = "personal_info",
        sensitive = true,
        examples = "1234567890"
    )
    val nationalCode: String? = null,
    
    @DataAgentField(
        description = "Place of birth",
        category = "personal_info",
        examples = "Tehran, New York"
    )
    val birthPlace: String? = null,
    
    @DataAgentField(
        description = "Date of birth",
        category = "personal_info",
        sensitive = true,
        examples = "1990-01-15"
    )
    val birthday: String? = null,
    
    @DataAgentField(
        description = "Father's name",
        category = "personal_info",
        sensitive = true,
        examples = "John Doe"
    )
    val fatherName: String? = null,
    
    @DataAgentField(
        description = "Mobile phone number",
        category = "contact",
        sensitive = true,
        examples = "+1-912-123-4567"
    )
    val mobileNumber: String? = null,
    
    @DataAgentField(
        description = "Employee or personnel code",
        category = "employment",
        examples = "EMP001, P12345"
    )
    val personnelCode: String? = null,

    @OneToOne(mappedBy = "profile")
    @DataAgentField(
        description = "Associated user entity",
        category = "relationship",
        examples = "User entity reference"
    )
    val user: User? = null,
    
    @DataAgentField(
        description = "Photo file identifier",
        category = "media",
        examples = "photo_123.jpg, avatar.png"
    )
    val photoFileId: String? = null
)
