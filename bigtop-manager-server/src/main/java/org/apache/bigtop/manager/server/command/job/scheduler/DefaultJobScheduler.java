package org.apache.bigtop.manager.server.command.job.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.dao.entity.Job;
import org.apache.bigtop.manager.server.command.job.runner.JobRunner;
import org.apache.bigtop.manager.server.command.job.runner.JobRunners;
import org.apache.bigtop.manager.server.holder.SessionUserHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class DefaultJobScheduler implements JobScheduler {

    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    private final Executor executor = Executors.newSingleThreadExecutor();

    private volatile boolean running = true;

    @Override
    public void submit(Job job) {
        Long userId = SessionUserHolder.getUserId();
        queue.offer(() -> {
            try {
                SessionUserHolder.setUserId(userId);
                JobRunner runner = JobRunners.getJobRunner(job);
                runner.run();
            } finally {
                SessionUserHolder.clear();
            }
        });
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
                    Runnable runnable = queue.take();
                    runnable.run();
                } catch (InterruptedException e) {
                    log.warn("Error when polling new job", e);
                }
            }
        });
    }
}
