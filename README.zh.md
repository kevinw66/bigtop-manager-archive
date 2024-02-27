# Bigtop-Manager

Bigtop-Manager 是一个用于管理 Bigtop 组件的平台。灵感来自 Apache Ambari。

## 先决条件

JDK：需要 JDK 17 或 21  
Metadata DB：Mariadb 或 Mysql

### API-文档

Swagger 用户界面

### 编译

```
mvn clean package -DskipTests
```

### 开发 人员

1. 创建数据库"bigtop_manager",配置数据库连接用户名和密码，默认均为“root”
2. 运行SQL DDL 脚本 `bigtop-manager-server/src/main/resources/ddl/MySQL-DDL-CREATE.sql`
3. 插入测试数据，数据脚本位于`dev-support/example/bigtop_manager/user.sql`
4. 启动 bigtop-manager-server `bigtop-manager-server/src/main/java/org/apache/bigtop/manager/server/ServerApplication.java`
5. 启动 bigtop-manager-agent `类似于启动bm-server`
6. 启动 bigtop-manager-ui `配置 nodejs 环境, 默认nodejs位于bigtop-manager-ui/node, 运行package.json`
7. 访问 `http://localhost:5173/`, 默认登录名和密码为 `"admin"`

### 如何测试服务

> 1. 登录
> 2. 创建群集 ->注册主机
> 3. 安装服务
> 4. 启动服务
> 5. 停止服务

### 如何测试 bm-monitoring
1. 安装 [Prometheus LTS Version](https://github.com/prometheus/prometheus/releases/download/v2.45.3/prometheus-2.45.3.linux-amd64.tar.gz)
2. 配置 prometheus.yml，在`scrape_configs`部分添加以下yaml代码
```
- job_name: "bm-agent-host"
  static_configs:
    - targets: ["agent server ip/hostname:8081"]
```