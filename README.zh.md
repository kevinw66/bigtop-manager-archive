# Bigtop-Manager

Bigtop-Manager 是一个用于管理 Bigtop 组件的平台。灵感来自 Apache Ambari。

## 先决条件

JDK：需要 JDK 17 或 21 Medata DB：Mariadb 或 Mysql

### API-文档

Swagger 用户界面

### 编译

```
mvn clean package -DskipTests
```

### 开发 人员

1. 配置数据库连接名称和密码，默认均为“root”
2. 源 SQL 文件 `dev-support/example/bigtop_manager` 
3. 运行 bigtop-manager-server `bigtop-manager-server/src/main/java/org/apache/bigtop/manager/server/ServerApplication.java` 
4. 运行 bigtop-manager-agent
5. 运行 bigtop-manager-ui `configure nodejs environmment, folder default is bigtop-manager-ui/node, then run with package.json` 
6. 启动bigtop-manager-ui后，访问“http://localhost:5173/”默认登录用户和密码 `"admin"` 

### 如何测试服务

> 1. 登录
> 2. 创建群集 ->注册主机
> 3. 安装服务
> 4. 启动服务
> 5. 停止服务