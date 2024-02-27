# Bigtop-Manager

Bigtop-Manager is a platform for managing Bigtop components. Inspired by Apache Ambari.

## Prerequisites

JDK: Requires JDK 17 or 21  
Metadata DB: Mariadb or Mysql

### API-DOCS
[swagger-ui](http://localhost:8080/swagger-ui/index.html)

### Compile
```bash
mvn clean package -DskipTests
```

### Developer
1. Create Database which named "bigtop_manager", Configure DB connect name & password, default both are 'root'
2. Run SQL DDL Script at `bigtop-manager-server/src/main/resources/ddl/MySQL-DDL-CREATE.sql`
3. Insert Test SQL Data at `dev-support/example/bigtop_manager/user.sql`
4. Start bigtop-manager-server `bigtop-manager-server/src/main/java/org/apache/bigtop/manager/server/ServerApplication.java`
5. Start bigtop-manager-agent `similiar with run bm-server`
6. Start bigtop-manager-ui `configure nodejs environmment, default folder is bigtop-manager-ui/node, then run with package.json`
7. Visit `http://localhost:5173/`, default login user & password are `"admin"`

### How to test a Service
> 1. Login
> 2. Create cluster ->Register host
> 3. Installation Services
> 4. Start Service
> 5. Stop Service

### How to test bm-monitoring
1. Install [Prometheus LTS Version](https://github.com/prometheus/prometheus/releases/download/v2.45.3/prometheus-2.45.3.linux-amd64.tar.gz)
2. Configure prometheus.yml, add below code into `scrape_configs`
```
- job_name: "bm-agent-host"
  static_configs:
    - targets: ["agent server ip/hostname:8081"]
```