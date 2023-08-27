package org.apache.bigtop.manager.server.configuration;

import com.google.common.eventbus.AsyncEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class EventBusConfig {

    @Bean
    public AsyncEventBus asyncEventBus() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10),
                new ThreadPoolExecutor.DiscardPolicy());
        return new AsyncEventBus(threadPoolExecutor);
    }
}
