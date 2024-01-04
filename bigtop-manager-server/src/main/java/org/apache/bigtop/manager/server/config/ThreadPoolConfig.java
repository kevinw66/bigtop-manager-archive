package org.apache.bigtop.manager.server.config;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.holder.SessionUserHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean("asyncServiceExecutor")
    public ThreadPoolTaskExecutor asyncRabbitTimeoutServiceExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setQueueCapacity(300);
        // Add Decorator
        threadPoolTaskExecutor.setTaskDecorator(new ContextCopyingDecorator());
        // Configure thread pool prefix
        threadPoolTaskExecutor.setThreadNamePrefix("AsyncEvent-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Slf4j
    static class ContextCopyingDecorator implements TaskDecorator {
        @Nonnull
        @Override
        public Runnable decorate(@Nonnull Runnable runnable) {
            // main thread
            Long userId = SessionUserHolder.getUserId();
            // sub thread
            return () -> {
                try {
                    // put userId into ThreadLocal
                    SessionUserHolder.setUserId(userId);
                    runnable.run();
                } finally {
                    SessionUserHolder.clear();
                }
            };
        }
    }
}