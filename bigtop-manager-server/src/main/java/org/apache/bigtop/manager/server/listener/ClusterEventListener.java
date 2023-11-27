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
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.event.ClusterCreateEvent;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
public class ClusterEventListener {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private SyncJobStrategy syncJobStrategy;

    @Async
    @TransactionalEventListener
    public void handleClusterCreate(ClusterCreateEvent event) {
        log.info("listen ClusterCreateEvent: {}", event);
        Long jobId = event.getJobId();
        Job job = jobRepository.getReferenceById(jobId);

        ClusterDTO clusterDTO = (ClusterDTO) event.getSource();

        // TODO temp code, just for test now
        Boolean failed = syncJobStrategy.handle(job, JobStrategyType.CONTINUE_ON_FAIL);

        if (!failed) {
            updateCluster(clusterDTO);
        }
    }

    private void updateCluster(ClusterDTO clusterDTO) {
        // Save cluster
        Cluster cluster = clusterRepository.findByClusterName(clusterDTO.getClusterName()).orElse(new Cluster());
        cluster.setState(MaintainState.INSTALLED);
        clusterRepository.save(cluster);

        // Save hosts
        List<Host> hosts = hostRepository.findAllByHostnameIn(clusterDTO.getHostnames());
        for (Host host : hosts) {
            host.setState(MaintainState.INSTALLED);
        }
        hostRepository.saveAll(hosts);
    }
}
