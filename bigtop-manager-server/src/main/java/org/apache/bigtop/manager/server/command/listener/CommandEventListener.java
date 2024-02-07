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
package org.apache.bigtop.manager.server.command.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.command.event.CommandEvent;
import org.apache.bigtop.manager.server.command.job.scheduler.JobScheduler;
import org.apache.bigtop.manager.dao.entity.Job;
import org.apache.bigtop.manager.dao.repository.JobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class CommandEventListener {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private JobScheduler jobScheduler;

    @Async
    @TransactionalEventListener
    public void listen(CommandEvent event) {
        Long jobId = event.getJobId();
        Job job = jobRepository.getReferenceById(jobId);

        jobScheduler.submit(job);
    }
}
