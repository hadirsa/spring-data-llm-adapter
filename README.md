# Data Agent

A modular Spring Boot library for intelligent database schema discovery and natural language query processing using LLM integration.

## Architecture

The project is structured into several modules:

- **`data-agent-core`**: Core functionality for schema discovery and management (technology-agnostic)
- **`data-agent-jpa`**: JPA-specific schema discovery implementation
- **`starters/`**: Spring Boot integration modules
  - **`data-agent-jpa-starter`**: Spring Boot starter for JPA functionality only
  - **`data-agent-starter`**: Complete Spring Boot starter (JPA + Generator)

## Project Structure

```
data-agent/
├── data-agent-core/           # Core functionality (technology-agnostic)
├── data-agent-jpa/            # JPA-specific schema discovery
├── starters/                   # Spring Boot integration modules
│   ├── data-agent-jpa-starter/        # JPA starter
│   └── data-agent-starter/            # Complete starter
├── examples/                   # Example applications
│   └── data-agent-jpa-example/       # Complete working example
├── pom.xml                    # Parent POM
└── README.md                  # This file
```

## Quick Start

### Option 1: Use the Complete Starter (Recommended)

Add the complete starter to your Spring Boot project:

```xml
<dependency>
    <groupId>com.dataagent</groupId>
    <artifactId>data-agent-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Option 2: Use Individual Starters

If you only need specific functionality:

```xml
<!-- For JPA schema discovery only -->
<dependency>
    <groupId>com.dataagent</groupId>
    <artifactId>data-agent-jpa-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

```

### Option 3: Use Individual Modules

For maximum control, use the individual modules:

```xml
<dependency>
    <groupId>com.dataagent</groupId>
    <artifactId>data-agent-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>com.dataagent</groupId>
    <artifactId>data-agent-jpa</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Configuration


## Usage

### 1. Mark Your Entities

```kotlin
@Entity
@Table(name = "users")
@DataAgent(description = "User entity for authentication and profile management")
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
@SpringBootApplication
@EnableDataAgent
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

## Features

### Schema Discovery
- Automatic scanning of `@DataAgent` annotated entities
- Multi-database support through Spring Data JPA
- Caching and performance optimization
- REST API for schema information

## Examples

See the `examples/` directory for complete working applications:

- `data-agent-jpa-example/` - Complete example with JPA and Executor functionality

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
cd examples/data-agent-jpa-example
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
