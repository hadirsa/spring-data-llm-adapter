# Data Agent JPA Example

This is an example application demonstrating how to use the Data Agent JPA module in a Spring Boot application.

## Overview

The Data Agent JPA module provides automatic schema discovery and learning for JPA entities. This example shows:

- How to enable Data Agent functionality with `@EnableDataAgent`
- How to annotate entities with `@DataAgent` for automatic discovery
- How to configure the Data Agent JPA module
- How to access the discovered schema information via REST API

## Features Demonstrated

- **Automatic Schema Discovery**: Entities annotated with `@DataAgent` are automatically discovered
- **Schema Learning**: The Data Agent learns the structure of your entities including fields, relationships, and metadata
- **REST API**: Access discovered schemas via HTTP endpoints
- **Configuration**: Customize Data Agent behavior through application properties

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Example

1. **Build the Data Agent modules first** (from the parent directory):
   ```bash
   mvn clean install
   ```

2. **Run the example application**:
   ```bash
   cd examples/data-agent-jpa-example
   mvn spring-boot:run
   ```

3. **Access the application**:
   - Application: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
   - Data Agent API: http://localhost:8080/api/data-agent
   - OpenAPI UI (Swagger): http://localhost:8080/swagger-ui.html (redirects to /swagger-ui/index.html)
   - OpenAPI JSON: http://localhost:8080/v3/api-docs
   - OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml

   Note: If you changed the server port in `application.yml`, replace `8080` accordingly.

### API Endpoints

Once the application is running, you can access the Data Agent API:

- `GET /api/data-agent/schemas` - Get all discovered schemas
- `GET /api/data-agent/schemas/{entityName}` - Get schema for specific entity
- `POST /api/data-agent/discover` - Manually trigger schema discovery
- `DELETE /api/data-agent/schemas` - Clear all learned schemas


## Example Entity

The example includes a sample entity `ExampleJpaEntity` that demonstrates:

- Basic JPA entity structure
- `@DataAgent` annotation usage
- Various field types and annotations
- Automatic schema discovery

```kotlin
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
    
    // ... other fields
)
```

## Database

This example uses H2 in-memory database for simplicity. The database is automatically created and populated with sample data when the application starts.

## Next Steps

- Add more complex entities with relationships
- Implement custom Data Agent features
- Integrate with your existing Spring Boot application
- Explore the REST API to understand the discovered schema structure

