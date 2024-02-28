package org.apache.bigtop.manager.agent.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.executor.CommandExecutor;
import org.apache.bigtop.manager.agent.executor.CommandExecutors;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class DefaultCommandScheduler implements CommandScheduler {

    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    private final Executor executor = Executors.newSingleThreadExecutor();

    private volatile boolean running = true;

    @Override
    public void submit(CommandRequestMessage message) {
        queue.offer(() -> {
            try {
                CommandExecutor commandExecutor = CommandExecutors.getCommandExecutor(message.getMessageType());
                commandExecutor.execute(message);
            } catch (Exception e) {
                log.error("Error when running command", e);
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
                    log.warn("Error when running command", e);
                }
            }
        });
    }
}
