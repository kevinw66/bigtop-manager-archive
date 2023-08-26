# Bigtop-Manager

Bigtop-Manager is a platform for managing Bigtop components.

## Development

Requires JDK 17

### API-DOCS
[swagger-ui](http://localhost:8080/swagger-ui/index.html)

### Compile
```bash
mvn clean package -DskipTests
```

### Example

1. source sql files in `dev-support/example/bigtop_manager`
2. run bigtop-manager-server
3. run bigtop-manager-agent
4. run bigtop-manager-ui

### How to test a Service
> 1. Login
> 2. Create cluster ->Register host
> 3. Installation Services
> 4. Execute Configuration Cache
> 5. Start Service
> 6. Stop Service
