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
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.model.event.JobCreateEvent;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.StageRepository;
import org.apache.bigtop.manager.server.orm.repository.TaskRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Component
public class JobEventListener {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    private static final String SCHEDULED_FIXED_DELAY = "5000";

    private static final Long LOCK_TIMEOUT = 3000L;

    private final List<Stage> runningStages = new ArrayList<>();

    private final List<Stage> waitingStages = new ArrayList<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Async
    @EventListener
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleJobCreate(JobCreateEvent event) {
        Long jobId = event.getJobId();
        Job job = jobRepository.getReferenceById(jobId);

        try {
            lock.writeLock().lock();
            waitingStages.addAll(job.getStages());
        } catch (Exception e) {
            log.error("Failed to handle job create event.", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Scheduled(fixedDelayString = SCHEDULED_FIXED_DELAY)
    public void schedule() {
        log.info("Scheduled at: " + System.currentTimeMillis());
        if (runningStages.isEmpty() && waitingStages.isEmpty()) {
            return;
        }

        try {
            boolean locked = lock.writeLock().tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
            if (!locked) {
                // Waiting for next time
                return;
            }

            runningStages.addAll(waitingStages);
            waitingStages.clear();
        } catch (InterruptedException e) {
            return;
        } finally {
            lock.writeLock().unlock();
        }

        run();
    }

    private void run() {
        Iterator<Stage> iterator = runningStages.iterator();
        while (iterator.hasNext()) {
            Stage stage = iterator.next();
            if (shouldRemoveStage(stage)) {
                iterator.remove();
                continue;
            }

            if (stage.getDependsOn() != null) {
                Stage parent = stageRepository.getReferenceById(stage.getDependsOn());
                if (parent.getState() == JobState.SUCCESSFUL) {
                    runStage(stage);
                }
            } else {
                runStage(stage);
            }
        }
    }

    private void runStage(Stage stage) {
        log.info("running... " + stage.getDesc());
//        List<Task> tasks = stage.getTasks();
        // 1. 更新状态

        // 2. 执行任务

        // 3. 更新状态
        // 4. 更新依赖
    }

    private Boolean shouldRemoveStage(Stage stage) {
        if (stage.getState() != JobState.PENDING) {
            return true;
        } else {
            if (stage.getDependsOn() != null) {
                // Check parent stage status.
                Stage parent = stageRepository.getReferenceById(stage.getDependsOn());
                if (parent.getState() == JobState.FAILED
                        || parent.getState() == JobState.TIMEOUT
                        || parent.getState() == JobState.CANCELED) {

                    stage.setState(JobState.CANCELED);
                    stageRepository.save(stage);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
