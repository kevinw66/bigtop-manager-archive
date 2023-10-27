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
import org.apache.bigtop.manager.common.message.type.HostCheckMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.event.ClusterCreateEvent;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.model.mapper.StackMapper;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class ClusterEventListener {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private RepoRepository repoRepository;

    @Resource
    private StackRepository stackRepository;

    @Async
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClusterCreate(ClusterCreateEvent event) {
        Long jobId = event.getJobId();
        Job job = jobRepository.getReferenceById(jobId);

        ClusterDTO clusterDTO = (ClusterDTO) event.getSource();

        // TODO temp code, just for test now
        Boolean failed = checkHosts(job);

        if (!failed) {
            persist(clusterDTO);
        }
    }

    private Boolean checkHosts(Job job) {
        // TODO temp code, we need to handle job/stage/task globally
        AtomicBoolean failed = new AtomicBoolean(false);
        Stage stage = job.getStages().get(0);

        stage.setState(JobState.PROCESSING);
        job.setState(JobState.PROCESSING);
        stageRepository.save(stage);
        jobRepository.save(job);

        List<Task> tasks = stage.getTasks();
        for (Task task : tasks) {
            task.setState(JobState.PROCESSING);
            taskRepository.save(task);

            String hostname = task.getHostname();
            HostCheckMessage hostCheckMessage = new HostCheckMessage();
            hostCheckMessage.setHostname(hostname);
            hostCheckMessage.setHostCheckTypes(HostCheckType.values());
            ResultMessage res = SpringContextHolder.getServerWebSocket().sendMessage(hostname, hostCheckMessage);
            if (res == null || res.getCode() != 0) {
                task.setState(JobState.FAILED);
                failed.set(true);
            } else {
                task.setState(JobState.SUCCESSFUL);
            }

            taskRepository.save(task);

            if (failed.get()) {
                stage.setState(JobState.FAILED);
                job.setState(JobState.FAILED);
            } else {
                stage.setState(JobState.SUCCESSFUL);
                job.setState(JobState.SUCCESSFUL);
            }
        }

        stageRepository.save(stage);
        jobRepository.save(job);

        return failed.get();
    }

    private void persist(ClusterDTO clusterDTO) {
        // Save cluster
        Stack stack = stackRepository.findByStackNameAndStackVersion(clusterDTO.getStackName(), clusterDTO.getStackVersion());
        StackDTO stackDTO = StackMapper.INSTANCE.Entity2DTO(stack);
        Cluster cluster = ClusterMapper.INSTANCE.DTO2Entity(clusterDTO, stackDTO, stack);
        clusterRepository.save(cluster);

        // Save hosts
        List<Host> hosts = new ArrayList<>();
        for (String hostname : clusterDTO.getHostnames()) {
            Host host = new Host();
            host.setCluster(cluster);
            host.setHostname(hostname);
            hosts.add(host);
        }

        hostRepository.saveAll(hosts);

        // Save repo
        List<Repo> repos = RepoMapper.INSTANCE.DTO2Entity(clusterDTO.getRepoInfoList(), cluster);
        repoRepository.saveAll(repos);
    }
}
