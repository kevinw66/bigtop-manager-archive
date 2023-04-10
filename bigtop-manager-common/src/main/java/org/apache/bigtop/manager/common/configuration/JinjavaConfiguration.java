package org.apache.bigtop.manager.common.configuration;

import com.hubspot.jinjava.Jinjava;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;


@Configuration
public class JinjavaConfiguration {


    @Bean
    public Jinjava jinjava() {
        return new Jinjava();
    }

}
