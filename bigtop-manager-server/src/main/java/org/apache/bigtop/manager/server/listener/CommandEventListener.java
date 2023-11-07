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
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.JobStrategyType;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.listener.strategy.AsyncJobStrategy;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
public class CommandEventListener {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private AsyncJobStrategy asyncJobStrategy;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommand(CommandEvent event) {
        log.info("listen CommandEvent: {}", event);
        Long jobId = event.getJobId();
        Job job = jobRepository.getReferenceById(jobId);

        Boolean failed = asyncJobStrategy.handle(job, JobStrategyType.OVER_ON_FAIL);
        log.info("[CommandEventListener] failed: {}", failed);

        if (!failed) {
            job.getStages().stream().flatMap(stage -> stage.getTasks().stream()).forEach(task -> {
                String content = task.getContent();
                RequestMessage requestMessage = JsonUtils.readFromString(content, RequestMessage.class);
                MessageType messageType = requestMessage.getMessageType();
                if (messageType == MessageType.COMMAND) {
                    CommandPayload commandPayload = JsonUtils.readFromString(requestMessage.getMessagePayload(), CommandPayload.class);
                    saveHostComponent(commandPayload.getComponentName(), commandPayload.getHostname(), commandPayload.getCommand());
                }
            });
        }
    }

    private void saveHostComponent(String componentName, String hostname, Command command) {
        HostComponent hostComponent = hostComponentRepository.findByComponentComponentNameAndHostHostname(componentName, hostname).orElse(new HostComponent());
        switch (command) {
            case INSTALL -> hostComponent.setState(MaintainState.INSTALLED);
            case START -> hostComponent.setState(MaintainState.STARTED);
            case STOP -> hostComponent.setState(MaintainState.STOPPED);
        }
        hostComponentRepository.save(hostComponent);
    }

}
