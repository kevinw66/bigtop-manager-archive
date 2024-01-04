package org.apache.bigtop.manager.server.config;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.holder.SessionUserHolder;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncThreadPoolConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setQueueCapacity(300);
        threadPoolTaskExecutor.setTaskDecorator(new ContextCopyingDecorator());
        threadPoolTaskExecutor.setThreadNamePrefix("AsyncTask-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Slf4j
    static class ContextCopyingDecorator implements TaskDecorator {
        @Nonnull
        @Override
        public Runnable decorate(@Nonnull Runnable runnable) {
            Long userId = SessionUserHolder.getUserId();
            return () -> {
                try {
                    SessionUserHolder.setUserId(userId);
                    runnable.run();
                } finally {
                    SessionUserHolder.clear();
                }
            };
        }
    }
}