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

set -e
bin=$(dirname "$0")

usage() {
    echo "usage: $PROG [start|stop|restart|status] component"
    echo "  commands:"
    echo "       -h, --help"
    exit 1
}

function log() {
    echo -e "[INFO] $(date) $1"
}

function start() {
    log "start"
    if [ $1 == 'ZOOKEEPER_SERVER' ]; then
        log "start ZOOKEEPER_SERVER"
        source ${ZOOKEEPER_CONF_DIR}/zookeeper-env.sh ; env ZOOCFGDIR=${ZOOKEEPER_CONF_DIR} ZOOCFG=zoo.cfg ${ZOOKEEPER_HOME}/bin/zkServer.sh start
    fi
}

function stop() {
    log "stop"
}

function restart() {
    log "restart"
}

function status() {
    log "status"
}

if [ -f "${bin}/service_env.sh" ]; then
    source "${bin}/service_env.sh"
else
    log "service_env.sh not exists!!!"
    exit 1
fi

while [ $# -gt 0 ]; do
    case "$1" in
    start)
        if [ $# -lt 2 ]; then
            usage
        fi
        start $2
        shift 2
        ;;
    stop)
        if [ $# -lt 2 ]; then
            usage
        fi
        stop $2
        shift 2
        ;;
    restart)
        if [ $# -lt 2 ]; then
            usage
        fi
        stop $2
        start $2
        shift 2
        ;;
    status)
        if [ $# -lt 2 ]; then
            usage
        fi
        status $2
        shift 2
        ;;
    -h | --help)
        usage
        shift
        ;;
    *)
        echo "Unknown argument: '$1'" 1>&2
        usage
        ;;
    esac
done
