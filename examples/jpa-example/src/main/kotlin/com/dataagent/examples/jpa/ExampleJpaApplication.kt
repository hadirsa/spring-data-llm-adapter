package ai.hadirsa.examples.jpa

import ai.hadirsa.spring.data.llm.adapter.annotation.EnableDataAgent
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License

/**
 * Example JPA application demonstrating the Data Agent JPA functionality.
 * 
 * To use this example:
 * 1. Add @EnableDataAgent to your main application class
 * 2. Optionally specify packages to scan: @EnableDataAgent(packages = "com.example.domain")
 * 3. Annotate your JPA entities with @DataAgent
 * 4. The Data Agent will automatically discover and learn your JPA entity schemas
 * 
 * Package scanning priority:
 * 1. Configuration properties (spring-data-llm-adapter.jpa.scan-packages)
 * 2. @EnableDataAgent annotation packages parameter
 * 3. Inferred from main application class
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = Info(
        title = "Spring Data LLM Adapter JPA Example API",
        version = "1.0",
        description = "Demonstrative Spring Boot app showcasing Spring Data LLM Adapter JPA auto-discovery.",
        contact = Contact(name = "Spring Data LLM Adapter Team"),
        license = License(name = "Apache-2.0")
    )
)
@EnableDataAgent(
    packages = "ai.hadirsa.examples.jpa.domain",
    autoDiscover = true
)
open class ExampleJpaApplication

fun main(args: Array<String>) {
    SpringApplication.run(ExampleJpaApplication::class.java, *args)
}
