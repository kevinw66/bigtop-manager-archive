package org.apache.bigtop.manager.agent.config;


import org.apache.bigtop.manager.stack.core.executor.Executor;
import org.apache.bigtop.manager.stack.core.executor.ExecutorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

    @Bean
    public Executor executor() {
        return new ExecutorImpl();
    }
}
