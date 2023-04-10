package org.apache.bigtop.manager.agent.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bigtop.manager.server")
public class ServerConfiguration {

    private String host;

    private Integer port;
}
