package org.apache.bigtop.manager.common.config;

import lombok.Data;
import org.apache.bigtop.manager.common.config.application.SerializerConfig;
import org.apache.bigtop.manager.common.config.application.ServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bigtop.manager")
public class ApplicationConfig {

    @NestedConfigurationProperty
    private ServerConfig server;

    @NestedConfigurationProperty
    private SerializerConfig serializer = new SerializerConfig();

    /**
     * Indicates whether the application is running in development mode, which is disabled by default.
     * In development mode, only NOP stacks are available and no real shell commands will be executed on the agent side.
     */
    private Boolean devMode = false;

}
