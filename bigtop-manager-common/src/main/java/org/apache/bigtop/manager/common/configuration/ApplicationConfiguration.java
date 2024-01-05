package org.apache.bigtop.manager.common.configuration;

import lombok.Data;
import org.apache.bigtop.manager.common.configuration.application.SerializerConfiguration;
import org.apache.bigtop.manager.common.configuration.application.ServerConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bigtop.manager")
public class ApplicationConfiguration {

    @NestedConfigurationProperty
    private ServerConfiguration server;

    @NestedConfigurationProperty
    private SerializerConfiguration serializer = new SerializerConfiguration();

}
