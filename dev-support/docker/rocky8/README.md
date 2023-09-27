<!--
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
## Build and install Bigtop-Manager by dev-support
Dev support is used to quickly develop and test bigtop-manager, which runs on the docker containers.

### **Step 1**: Install build tools: Git、Docker
The scripts require docker to be installed, since the compile process will run in a docker container and Bigtop-Manager cluster also deploys on containers.

**RHEL (Rocky 8) :**
```shell
yum install -y git docker
```
### **Step 2**: Download Bigtop-Manager source
```shell
git clone https://github.com/kevinw66/bigtop-manager.git
```
> You need to change the `node.version` in the `pom.xml` file under the `bigtop-manager-ui` module to `16.x`

### **Step 3**: Enter workspace
**RHEL (Rocky 8) :**
```shell
cd bigtop-manager/dev-support/docker/rocky8/
```
### **Step 4**: Build develop basic image
Run the setup command, you will get `bigtop-manager/develop:trunk-rocky-8` image. It has the enviroment needed to compile Bigtop-Manager and run servers such as Bigtop-Manager Server, Bigtop-Manager Agent, Mysql, etc.

**RHEL (Rocky 8) :**
```shell
./build-image.sh
```
### **Step 5**: Build Bigtop-Manager source & create Bigtop-Manager cluster
* The first compilation will take about 1 hour to download resources, and the next compilation will directly use the maven cache.
* Bigtop-Manager UI、Bigtop-Manager Server Debug Port、MariaDB Server are also exposed to local ports: 8080、5005、3306.
* Docker host names are: bigtop-manager-server、bigtop-manager-agent-01、bigtop-manager-agent-02.
* Access admin page via http://localhost:8080 on your web browser. Log in with username `admin` and password `admin`.
* Extra configurations are in `build-containers.sh` last few lines, eg. Kerberos Configuration、Hive DB Configuration.

**RHEL (Rocky 8) :**
```shell
./build-containers.sh
```
### **Step 6**: Redistribution stack
Re-distribute stack scripts without re-create clusters.

**RHEL (Rocky 8) :**
```shell
./distribute-scripts.sh
```
### **Step 7**: Clean Bigtop-Manager cluster
Clean up the containers of Bigtop-Manager cluster when you are done developing or testing.

**RHEL (Rocky 8) :**
```shell
./clear-containers.sh
```
### Step 8: Clean up the build environment
**Note :** This operation will completely delete maven cache.

**RHEL (Rocky 8) :**
```shell
docker rm -f bigtop-manager-build
```
