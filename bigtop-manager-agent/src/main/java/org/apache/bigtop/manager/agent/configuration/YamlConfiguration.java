package org.apache.bigtop.manager.agent.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

@Configuration
public class YamlConfiguration {

    @Bean
    public Yaml yaml() {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        return new Yaml(representer);
    }
}
