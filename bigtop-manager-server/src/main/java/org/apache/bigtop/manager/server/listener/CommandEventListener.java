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

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.enums.StatusType;
import org.apache.bigtop.manager.server.enums.heartbeat.CommandState;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.mapper.TaskMapper;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.ws.Callback;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.apache.bigtop.manager.common.constants.Constants.COMMAND_MESSAGE_RESPONSE_TIMEOUT;

@Slf4j
@Component
public class CommandEventListener implements Callback {

    @Resource
    private AsyncEventBus asyncEventBus;

    @PostConstruct
    public void init() {
        asyncEventBus.register(this);
    }

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private CommandLogRepository commandLogRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    private CountDownLatch countDownLatch;

    //    @Async
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Subscribe
    public void handleCommand(CommandEvent event) {
        log.info("listen CommandEvent: {}", event);
        Long jobId = event.getJobId();
//        CommandDTO commandDTO = (CommandDTO) event.getSource();
        Job job = jobRepository.getReferenceById(jobId);
        // Job state change to processing
        job.setState(JobState.PROCESSING);
        jobRepository.save(job);

        List<Stage> stages = job.getStages();

        LinkedBlockingQueue<Stage> pipeLineQueue = new LinkedBlockingQueue<>(stages);
        while (!pipeLineQueue.isEmpty()) {
            Stage stage = pipeLineQueue.poll();
            log.info("starting execute task flow");
            // Stage state change to processing
            stage.setState(JobState.PROCESSING);
            stageRepository.save(stage);

            for (Task task : stage.getTasks()) {
                CommandMessage commandMessage = TaskMapper.INSTANCE.DTO2CommandMessage(task);
                log.info("commandMessage: {}", commandMessage);

                SpringContextHolder.getServerWebSocket().sendMessage(task.getHostname(), commandMessage, this);

                // Task state change to processing
                task.setMessageId(commandMessage.getMessageId());
                task.setState(JobState.PROCESSING);
                taskRepository.save(task);
            }

            countDownLatch = new CountDownLatch(stage.getTasks().size());

            boolean timeoutFlag;
            try {
                timeoutFlag = countDownLatch.await(COMMAND_MESSAGE_RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (!timeoutFlag) {
                log.error("execute task timeout");
                stage.setState(JobState.TIMEOUT);
                stageRepository.save(stage);
                job.setState(JobState.FAILED);
                jobRepository.save(job);

                releaseRemainStages(pipeLineQueue);
            } else {
                stage.setState(JobState.SUCCESSFUL);
                stageRepository.save(stage);
            }

        }

        job.setState(JobState.SUCCESSFUL);
        jobRepository.save(job);
    }

    public void call(ResultMessage resultMessage) {
        log.info("Execute command completed, {}", resultMessage);
        countDownLatch.countDown();

        Task task = taskRepository.findById(resultMessage.getTaskId()).orElse(new Task());

        String componentName = task.getComponentName();
        String hostname = task.getHostname();
        Command command = task.getCommand();

        saveCommandLog(resultMessage.getResult(), task);

        //TODO: Send success or failure messages to the frontend
        if (resultMessage.getCode() == MessageConstants.SUCCESS_CODE) {
            log.info("Execute Task {}. taskId: {}", JobState.SUCCESSFUL, task.getId());
            task.setState(JobState.SUCCESSFUL);
            taskRepository.save(task);

            saveHostComponent(componentName, hostname, command);
        } else {
            log.info("Execute Task {}, Cancel other stages. taskId: {}", JobState.FAILED, task.getId());
            Stage stage = task.getStage();
            stage.setState(JobState.FAILED);
            stageRepository.save(stage);
            Job job = task.getJob();
            job.setState(JobState.FAILED);
            jobRepository.save(job);

            // Updating the current task status to FAILED
            // Pop up all subsequent tasks and assign CANCELED status
        }

    }

    /**
     * Release remaining Stages
     * Execute when failed or timeout
     */
    private void releaseRemainStages(LinkedBlockingQueue<Stage> pipeLineQueue) {
        if (!pipeLineQueue.isEmpty()) {
            List<Stage> remainStages = new ArrayList<>(pipeLineQueue.size());
            pipeLineQueue.drainTo(remainStages);
            for (Stage stage : remainStages) {
                // Setting the status of the remaining stages to CANCELED
                stage.setState(JobState.CANCELED);
                stageRepository.save(stage);

                stage.getTasks().forEach(t -> t.setState(JobState.CANCELED));
                taskRepository.saveAll(stage.getTasks());
            }
        }
    }

    private void saveCommandLog(String result, Task task) {
        CommandLog commandLog = new CommandLog();
        commandLog.setTask(task);
        commandLog.setStage(task.getStage());
        commandLog.setJob(task.getJob());
        commandLog.setHostname(task.getHostname());
        commandLog.setResult(result);
        commandLogRepository.save(commandLog);
    }

    private void saveHostComponent(String componentName, String hostname, Command command) {
        HostComponent hostComponent = hostComponentRepository.findByComponentComponentNameAndHostHostname(componentName, hostname)
                .orElse(new HostComponent());
        switch (command) {
            case INSTALL -> {
                hostComponent.setStatus(StatusType.INSTALLED.getCode());
                hostComponent.setState(CommandState.INSTALLED);
            }
            case START -> hostComponent.setState(CommandState.STARTED);
            case STOP -> hostComponent.setState(CommandState.STOPPED);
        }
        hostComponentRepository.save(hostComponent);
    }

}
