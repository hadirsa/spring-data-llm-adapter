# Spring Data LLM Adapter

A library for intelligent database schema discovery and natural language query processing using LLM integration.

## Architecture

The project is structured into several modules:

- **`spring-data-llm-adapter`**: Core functionality for schema discovery and management (technology-agnostic)
- **`spring-data-llm-adapter-jpa`**: JPA-specific schema discovery implementation
- **`starters/`**: Spring Boot integration modules
  - **`spring-data-llm-adapter-jpa-starter`**: Spring Boot starter for JPA functionality only
  - **`spring-data-llm-adapter-starter`**: Complete Spring Boot starter (JPA + future modules)

## Project Structure

```
spring-data-llm-adapter/
├── spring-data-llm-adapter/           # Core functionality (technology-agnostic)
├── spring-data-llm-adapter-jpa/            # JPA-specific schema discovery
├── starters/                   # Spring Boot integration modules
│   ├── spring-data-llm-adapter-jpa-starter/        # JPA starter
│   └── spring-data-llm-adapter-starter/            # Complete starter
├── examples/                   # Example applications
│   └── jpa-example/       # Complete working example
├── pom.xml                    # Parent POM
└── README.md                  # This file
```

## Quick Start

### Option 1: Use the Complete Starter (Recommended)

Add the complete starter to your Spring Boot project:

```xml
<dependency>
    <groupId>ai.hadirsa</groupId>
    <artifactId>spring-data-llm-adapter-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Option 2: Use Individual Starters

If you only need specific functionality:

```xml
<!-- For JPA schema discovery only -->
<dependency>
    <groupId>ai.hadirsa</groupId>
    <artifactId>spring-data-llm-adapter-jpa-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

```

### Option 3: Use Individual Modules

For maximum control, use the individual modules:

```xml
<dependency>
    <groupId>ai.hadirsa</groupId>
    <artifactId>spring-data-llm-adapter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>ai.hadirsa</groupId>
    <artifactId>spring-data-llm-adapter-jpa</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Configuration


## Usage

### 1. Mark Your Entities

```kotlin
@DataAgent(
  description = "User entity for authentication and profile management",
  discoverable = true
)
@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "username", unique = true, nullable = false)
    val username: String,
    
    @Column(name = "email", unique = true, nullable = false)
    val email: String
)
```

### 2. Enable Data Agent

```kotlin
@EnableDataAgent(
  packages = "ai.hadirsa.examples.jpa.domain",
  autoDiscover = true
)
@SpringBootApplication
class YourApplication

fun main(args: Array<String>) {
    runApplication<YourApplication>(*args)
}
```

## API Endpoints

### Schema Discovery
- `GET /api/jpa/schemas` - Get all discovered schemas
- `POST /api/jpa/discover` - Trigger schema discovery
- `GET /api/jpa/schemas/{className}` - Get specific schema

Example responses (Profile entity):
```json
{
  "entityClassName": "ai.hadirsa.examples.jpa.domain.Profile",
  "tableName": "UM_PROFILE",
  "description": "Example JPA entity Profile for demonstration purposes",
  "fields": [
    {
      "fieldName": "id",
      "columnName": "id",
      "fieldType": "String",
      "columnType": "VARCHAR",
      "nullable": true,
      "unique": false,
      "length": 255,
      "precision": null,
      "scale": null,
      "description": "Unique identifier for the profile",
      "examples": "uuid-string",
      "category": "identification",
      "primaryKey": true
    },
    {
      "fieldName": "ssn",
      "columnName": "ssn",
      "fieldType": "String",
      "columnType": "VARCHAR",
      "nullable": true,
      "unique": true,
      "length": null,
      "precision": null,
      "scale": null,
      "description": "Social security number",
      "examples": "123-45-6789",
      "category": "personal_info",
      "primaryKey": false
    },
    
   ...
    
    
  ],
  "relationships": [
    {
      "fieldName": "user",
      "targetEntity": "ai.hadirsa.examples.jpa.domain.User",
      "relationshipType": "OneToOne",
      "mappedBy": "profile",
      "joinColumn": null,
      "cascadeTypes": [],
      "fetchType": "EAGER"
    }
  ]
}
```
## Features

### Schema Discovery
- Automatic scanning of `@DataAgent` annotated entities
- Multi-database support through Spring Data JPA
- Caching and performance optimization
- REST API for schema information

## Examples

See the `examples/` directory for complete working applications:

- `jpa-example/` - Complete example with JPA and Executor functionality

## Development

### Building the Project

```bash
./mvnw clean install
```

### Running Tests

```bash
./mvnw test
```

### Running the Example

```bash
cd examples/jpa-example
./mvnw spring-boot:run
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details. 
