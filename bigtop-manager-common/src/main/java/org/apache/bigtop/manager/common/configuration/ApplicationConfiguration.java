package org.apache.bigtop.manager.common.configuration;

import lombok.Data;
import org.apache.bigtop.manager.common.configuration.embbed.SerializerConfiguration;
import org.apache.bigtop.manager.common.configuration.embbed.ServerConfiguration;
import org.apache.bigtop.manager.common.configuration.embbed.StackConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:common.properties")
@ConfigurationProperties(prefix = "bigtop.manager")
public class ApplicationConfiguration {

    private ServerConfiguration server;

    private SerializerConfiguration serializer;

    private StackConfiguration stack;
}
