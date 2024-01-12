package org.apache.bigtop.manager.common.utils;

import jakarta.annotation.Nonnull;
import org.apache.bigtop.manager.common.config.ApplicationConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class Environments implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        Environments.applicationContext = applicationContext;
    }

    public static Boolean isDevMode() {
        return applicationContext.getBean(ApplicationConfig.class).getDevMode();
    }
}
