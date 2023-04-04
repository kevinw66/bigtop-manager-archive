package org.apache.bigtop.manager.server.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI apiV1Info1() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bigtop Manager Api Docs")
                        .description("Bigtop Manager Api Docs")
                        .version("V1"));
    }

}
