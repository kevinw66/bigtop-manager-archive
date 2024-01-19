# Bigtop-Manager

Bigtop-Manager is a platform for managing Bigtop components. Provide similar functions with Ambari

## Prerequisites

JDK: Requires JDK 17 or 21
Medata DB: Mariadb or Mysql

### API-DOCS
[swagger-ui](http://localhost:8080/swagger-ui/index.html)

### Compile
```bash
mvn clean package -DskipTests
```

### Developer
1. Configure DB connect name & password, default both are 'root'
2. source sql files in `dev-support/example/bigtop_manager`
3. run bigtop-manager-server `bigtop-manager-server/src/main/java/org/apache/bigtop/manager/server/ServerApplication.java`
4. run bigtop-manager-agent
5. run bigtop-manager-ui `configure nodejs environmment, folder default is bigtop-manager-ui/node, then run with package.json`
6. after started bigtop-manager-ui, visit "http://localhost:5173/" default login user & password are `"admin"`

### How to test a Service
> 1. Login
> 2. Create cluster ->Register host
> 3. Installation Services
> 4. Start Service
> 5. Stop Service
