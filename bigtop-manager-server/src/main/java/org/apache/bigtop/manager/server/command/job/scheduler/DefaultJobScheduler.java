package org.apache.bigtop.manager.server.command.job.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.command.job.runner.JobRunner;
import org.apache.bigtop.manager.server.command.job.runner.JobRunners;
import org.apache.bigtop.manager.dao.entity.Job;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class DefaultJobScheduler implements JobScheduler {

    private final LinkedBlockingQueue<Job> queue = new LinkedBlockingQueue<>();

    private final Executor executor = Executors.newSingleThreadExecutor();

    private volatile boolean running = true;

    @Override
    public void submit(Job job) {
        queue.offer(job);
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @PostConstruct
    public void init() {
        executor.execute(() -> {
            while (running) {
                try {
                    Job job = queue.take();
                    JobRunner runner = JobRunners.getJobRunner(job);
                    runner.run();
                } catch (InterruptedException e) {
                    log.warn("Error when polling new job", e);
                }
            }
        });
    }
}
