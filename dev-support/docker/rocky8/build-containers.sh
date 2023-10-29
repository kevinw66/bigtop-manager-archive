#!/bin/bash

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

BIN_DIR=$(dirname $0)
cd $BIN_DIR
echo $PWD

echo -e "\033[32mStarting container bigtop-manager-build-r8\033[0m"
if [[ -z $(docker ps -a --format "table {{.Names}}" | grep "bigtop-manager-build-r8") ]];then
  docker run -it -d --name bigtop-manager-build-r8 -v $PWD/../../../:/opt/develop/bigtop-manager \
    -w /opt/develop/bigtop-manager \
    bigtop-manager/develop:trunk-rocky-8
else
  docker start bigtop-manager-build-r8
fi

echo -e "\033[32mCompiling bigtop-manager\033[0m"
docker exec bigtop-manager-build-r8 bash -c "mvn clean package -DskipTests"
docker stop bigtop-manager-build-r8

echo -e "\033[32mCreating network bigtop-manager\033[0m"
docker network create --driver bridge bigtop-manager

echo -e "\033[32mCreating container bigtop-manager-mysql\033[0m"
docker run -d -it -p 13306:3306 --name bigtop-manager-mysql --hostname bigtop-manager-mysql --network bigtop-manager -e MYSQL_DATABASE=bigtop_manager -e MYSQL_ROOT_PASSWORD=root mysql:5.7.44 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

echo -e "\033[32mCreating container bigtop-manager-server\033[0m"
docker run -d -it -p 15005:5005 -p 18080:8080 --name bigtop-manager-server --hostname bigtop-manager-server --network bigtop-manager --cap-add=SYS_TIME bigtop-manager/develop:trunk-rocky-8
docker cp ../../../bigtop-manager-server/target/bigtop-manager-server bigtop-manager-server:/opt/
docker cp ../../../bigtop-manager-agent/target/bigtop-manager-agent bigtop-manager-server:/opt/
SERVER_PUB_KEY=`docker exec bigtop-manager-server /bin/cat /root/.ssh/id_rsa.pub`
docker exec bigtop-manager-server bash -c "echo '$SERVER_PUB_KEY' > /root/.ssh/authorized_keys"
docker exec bigtop-manager-server ssh-keygen -N '' -t rsa -b 2048 -f /etc/ssh/ssh_host_rsa_key
docker exec bigtop-manager-server ssh-keygen -N '' -t ecdsa -b 256 -f /etc/ssh/ssh_host_ecdsa_key
docker exec bigtop-manager-server ssh-keygen -N '' -t ed25519 -b 256 -f /etc/ssh/ssh_host_ed25519_key
docker exec bigtop-manager-server /usr/sbin/sshd
docker exec bigtop-manager-server rm -f /run/nologin

echo -e "\033[32mCreating container bigtop-manager-agent-01\033[0m"
docker run -d -it --name bigtop-manager-agent-01 --hostname bigtop-manager-agent-01 --network bigtop-manager --cap-add=SYS_TIME bigtop-manager/develop:trunk-rocky-8
docker cp ../../../bigtop-manager-agent/target/bigtop-manager-agent bigtop-manager-agent-01:/opt/
docker exec bigtop-manager-agent-01 bash -c "echo '$SERVER_PUB_KEY' > /root/.ssh/authorized_keys"
docker exec bigtop-manager-agent-01 ssh-keygen -N '' -t rsa -b 2048 -f /etc/ssh/ssh_host_rsa_key
docker exec bigtop-manager-agent-01 ssh-keygen -N '' -t ecdsa -b 256 -f /etc/ssh/ssh_host_ecdsa_key
docker exec bigtop-manager-agent-01 ssh-keygen -N '' -t ed25519 -b 256 -f /etc/ssh/ssh_host_ed25519_key
docker exec bigtop-manager-agent-01 /usr/sbin/sshd
docker exec bigtop-manager-agent-01 rm -f /run/nologin

echo -e "\033[32mCreating container bigtop-manager-agent-02\033[0m"
docker run -d -it --name bigtop-manager-agent-02 --hostname bigtop-manager-agent-02 --network bigtop-manager --cap-add=SYS_TIME bigtop-manager/develop:trunk-rocky-8
docker cp ../../../bigtop-manager-agent/target/bigtop-manager-agent bigtop-manager-agent-02:/opt/
docker exec bigtop-manager-agent-02 bash -c "echo '$SERVER_PUB_KEY' > /root/.ssh/authorized_keys"
docker exec bigtop-manager-agent-02 ssh-keygen -N '' -t rsa -b 2048 -f /etc/ssh/ssh_host_rsa_key
docker exec bigtop-manager-agent-02 ssh-keygen -N '' -t ecdsa -b 256 -f /etc/ssh/ssh_host_ecdsa_key
docker exec bigtop-manager-agent-02 ssh-keygen -N '' -t ed25519 -b 256 -f /etc/ssh/ssh_host_ed25519_key
docker exec bigtop-manager-agent-02 /usr/sbin/sshd
docker exec bigtop-manager-agent-02 rm -f /run/nologin

echo -e "\033[32mConfiguring hosts file\033[0m"
BIGTOP_MANAGER_MYSQL_IP=`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' bigtop-manager-mysql`
BIGTOP_MANAGER_SERVER_IP=`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' bigtop-manager-server`
BIGTOP_MANAGER_AGENT_01_IP=`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' bigtop-manager-agent-01`
BIGTOP_MANAGER_AGENT_02_IP=`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' bigtop-manager-agent-02`
docker exec bigtop-manager-server bash -c "echo '$BIGTOP_MANAGER_AGENT_01_IP      bigtop-manager-agent-01' >> /etc/hosts"
docker exec bigtop-manager-server bash -c "echo '$BIGTOP_MANAGER_AGENT_02_IP      bigtop-manager-agent-02' >> /etc/hosts"
docker exec bigtop-manager-server bash -c "echo '$BIGTOP_MANAGER_MYSQL_IP      bigtop-manager-mysql' >> /etc/hosts"
docker exec bigtop-manager-agent-01 bash -c "echo '$BIGTOP_MANAGER_SERVER_IP      bigtop-manager-server' >> /etc/hosts"
docker exec bigtop-manager-agent-01 bash -c "echo '$BIGTOP_MANAGER_AGENT_02_IP      bigtop-manager-agent-02' >> /etc/hosts"
docker exec bigtop-manager-agent-01 bash -c "echo '$BIGTOP_MANAGER_MYSQL_IP      bigtop-manager-mysql' >> /etc/hosts"
docker exec bigtop-manager-agent-02 bash -c "echo '$BIGTOP_MANAGER_SERVER_IP      bigtop-manager-server' >> /etc/hosts"
docker exec bigtop-manager-agent-02 bash -c "echo '$BIGTOP_MANAGER_AGENT_01_IP      bigtop-manager-agent-01' >> /etc/hosts"
docker exec bigtop-manager-agent-01 bash -c "echo '$BIGTOP_MANAGER_MYSQL_IP      bigtop-manager-mysql' >> /etc/hosts"

echo -e "\033[32mSynchronize Chrony\033[0m"
docker exec bigtop-manager-server bash -c "chronyd -u root -f /etc/chrony.conf"
docker exec bigtop-manager-server bash -c "chronyc tracking"
docker exec bigtop-manager-agent-01 bash -c "chronyd -u root -f /etc/chrony.conf"
docker exec bigtop-manager-agent-01 bash -c "chronyc tracking"
docker exec bigtop-manager-agent-02 bash -c "chronyd -u root -f /etc/chrony.conf"
docker exec bigtop-manager-agent-02 bash -c "chronyc tracking"

echo -e "\033[32mServer Ip Setting\033[0m"
docker exec bigtop-manager-server bash -c "sed -i 's/localhost:3306/$BIGTOP_MANAGER_MYSQL_IP:3306/' /opt/bigtop-manager-server/conf/application.properties"
docker exec bigtop-manager-server bash -c "sed -i 's/bigtop.manager.server.host=localhost/bigtop.manager.server.host=$BIGTOP_MANAGER_SERVER_IP/' /opt/bigtop-manager-agent/conf/application.properties"
docker exec bigtop-manager-agent-01 bash -c "sed -i 's/bigtop.manager.server.host=localhost/bigtop.manager.server.host=$BIGTOP_MANAGER_SERVER_IP/' /opt/bigtop-manager-agent/conf/application.properties"
docker exec bigtop-manager-agent-02 bash -c "sed -i 's/bigtop.manager.server.host=localhost/bigtop.manager.server.host=$BIGTOP_MANAGER_SERVER_IP/' /opt/bigtop-manager-agent/conf/application.properties"

docker exec bigtop-manager-server bash -c "nohup /bin/bash /opt/bigtop-manager-server/bin/start.sh --debug > /dev/null 2>&1 &"
docker exec bigtop-manager-server bash -c "nohup /bin/bash /opt/bigtop-manager-agent/bin/start.sh > /dev/null 2>&1 &"
docker exec bigtop-manager-agent-01 bash -c "nohup /bin/bash /opt/bigtop-manager-agent/bin/start.sh > /dev/null 2>&1 &"
docker exec bigtop-manager-agent-02 bash -c "nohup /bin/bash /opt/bigtop-manager-agent/bin/start.sh > /dev/null 2>&1 &"

echo -e "\033[32mPrint Bigtop-Manager Server RSA Private Key\033[0m"
docker exec bigtop-manager-server bash -c "cat ~/.ssh/id_rsa"

# MySQL HOST: bigtop-manager-mysql
# MySQL PORT: 3306
# DATABASE NAME: bigtop_manager
# DATABASE USER NAME: root
# DATABASE PASSWORD: root

