package org.apache.bigtop.manager.agent.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bigtop.manager.stack")
public class StackConfiguration {

    @Value(value = "${bigtop.manager.stack.cache.dir}")
    private String cacheDir;
    @Value(value = "${bigtop.manager.stack.env.file}")
    private String envFile;
}
