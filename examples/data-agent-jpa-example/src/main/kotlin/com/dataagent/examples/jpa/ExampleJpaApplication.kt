package com.dataagent.examples.jpa

import com.dataagent.jpa.annotation.EnableDataAgent
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
 * 2. Annotate your JPA entities with @DataAgent
 * 3. Configure the base package in application.yml
 * 4. The Data Agent will automatically discover and learn your JPA entity schemas
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = Info(
        title = "Data Agent JPA Example API",
        version = "1.0",
        description = "Demonstrative Spring Boot app showcasing Data Agent JPA auto-discovery and executor endpoints.",
        contact = Contact(name = "Data Agent Team"),
        license = License(name = "Apache-2.0")
    )
)
@EnableDataAgent
open class ExampleJpaApplication

fun main(args: Array<String>) {
    SpringApplication.run(ExampleJpaApplication::class.java, *args)
}
