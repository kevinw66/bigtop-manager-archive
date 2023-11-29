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
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.event.ClusterCreateEvent;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
public class ClusterEventListener {

    @Resource
    private StackRepository stackRepository;

    @Resource
    private RepoRepository repoRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private SyncJobStrategy syncJobStrategy;

    @Resource
    private HostAddEventListener hostAddEventListener;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClusterCreate(ClusterCreateEvent event) {
        log.info("listen ClusterCreateEvent: {}", event);
        Long jobId = event.getJobId();
        Job job = jobRepository.getReferenceById(jobId);

        ClusterDTO clusterDTO = (ClusterDTO) event.getSource();

        // TODO temp code, just for test now
        Boolean failed = syncJobStrategy.handle(job, JobStrategyType.CONTINUE_ON_FAIL);

        if (!failed) {
            Cluster cluster = saveCluster(clusterDTO);
            updateJob(job, cluster);
        }
    }

    private void updateJob(Job job, Cluster cluster) {
        job.setCluster(cluster);
        for (Stage stage : job.getStages()) {
            stage.setCluster(cluster);
            for (Task task : stage.getTasks()) {
                task.setCluster(cluster);
            }
            taskRepository.saveAll(stage.getTasks());
        }
        stageRepository.saveAll(job.getStages());
        jobRepository.save(job);
    }

    private Cluster saveCluster(ClusterDTO clusterDTO) {
        // Save cluster
        Stack stack = stackRepository.findByStackNameAndStackVersion(clusterDTO.getStackName(), clusterDTO.getStackVersion());
        StackDTO stackDTO = StackUtils.getStackKeyMap().get(StackUtils.fullStackName(clusterDTO.getStackName(), clusterDTO.getStackVersion())).getLeft();
        Cluster cluster = ClusterMapper.INSTANCE.fromDTO2Entity(clusterDTO, stackDTO, stack);
        cluster.setSelected(clusterRepository.count() == 0);
        cluster.setState(MaintainState.INSTALLED);

        Cluster oldCluster = clusterRepository.findByClusterName(clusterDTO.getClusterName()).orElse(new Cluster());
        if (oldCluster.getId() != null) {
            cluster.setId(oldCluster.getId());
        }
        clusterRepository.save(cluster);

        hostAddEventListener.saveHost(cluster, clusterDTO.getHostnames());

        // Save repo
        List<Repo> repos = RepoMapper.INSTANCE.fromDTO2Entity(clusterDTO.getRepoInfoList(), cluster);
        List<Repo> oldRepos = repoRepository.findAllByCluster(cluster);

        for (Repo repo : repos) {
            for (Repo oldRepo : oldRepos) {
                if (oldRepo.getArch().equals(repo.getArch()) && oldRepo.getOs().equals(repo.getOs())) {
                    repo.setId(oldRepo.getId());
                }
            }
        }

        repoRepository.saveAll(repos);
        return cluster;
    }
}
