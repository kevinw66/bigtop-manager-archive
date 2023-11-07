/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.bigtop.manager.server.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.JobStrategyType;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.listener.strategy.SyncJobStrategy;
import org.apache.bigtop.manager.server.model.event.HostAddEvent;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
public class HostAddEventListener {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private SyncJobStrategy syncJobStrategy;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleHostAdd(HostAddEvent event) {
        log.info("listen HostAddEvent: {}", event);
        Long jobId = event.getJobId();
        Job job = jobRepository.getReferenceById(jobId);

        List<String> hostnames = event.getHostnames();
        // TODO temp code, just for test now
        Boolean failed = syncJobStrategy.handle(job, JobStrategyType.CONTINUE_ON_FAIL);

        if (!failed) {
            saveHost(hostnames, job.getCluster().getId());
        }
    }

    private void saveHost(List<String> hostnames, Long clusterId) {
        List<Host> hosts = hostRepository.findAllByClusterIdAndHostnameIn(clusterId, hostnames);
        for (Host host : hosts) {
            host.setState(MaintainState.INSTALLED);
        }
        hostRepository.saveAll(hosts);
    }

}
