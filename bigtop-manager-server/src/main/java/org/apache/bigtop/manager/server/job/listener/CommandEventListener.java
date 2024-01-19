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
package org.apache.bigtop.manager.server.job.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobStrategyType;
import org.apache.bigtop.manager.server.job.event.CommandEvent;
import org.apache.bigtop.manager.server.job.strategy.AsyncJobStrategy;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.StageRepository;
import org.apache.bigtop.manager.server.orm.repository.TaskRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class CommandEventListener {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private AsyncJobStrategy asyncJobStrategy;

    @Async
    @TransactionalEventListener
    public void listen(CommandEvent event) {
        log.info("listen CommandEvent: {}", event);
        CommandDTO commandDTO = (CommandDTO) event.getSource();
        Long jobId = event.getJobId();
        Job job = jobRepository.getReferenceById(jobId);

        Boolean failed = asyncJobStrategy.handle(job, JobStrategyType.OVER_ON_FAIL);
        log.info("[CommandEventListener] failed: {}", failed);

        if (!failed) {
            afterJobSuccess(job, commandDTO);
        }
    }

    private void afterJobSuccess(Job job, CommandDTO commandDTO) {
        CommandLevel commandLevel = commandDTO.getCommandLevel();
        Command command = commandDTO.getCommand();
        if (commandLevel == CommandLevel.CLUSTER && command == Command.INSTALL) {
            // Link job to cluster after cluster successfully added
            Cluster cluster = clusterRepository.findByClusterName(commandDTO.getClusterCommand().getClusterName()).orElse(new Cluster());
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
    }
}
