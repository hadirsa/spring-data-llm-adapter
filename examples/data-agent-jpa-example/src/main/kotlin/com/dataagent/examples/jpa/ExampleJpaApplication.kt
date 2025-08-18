package com.dataagent.examples.jpa

import com.dataagent.jpa.annotation.EnableDataAgent
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

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
@EnableDataAgent
open class ExampleJpaApplication

fun main(args: Array<String>) {
    SpringApplication.run(ExampleJpaApplication::class.java, *args)
}
