package org.apache.bigtop.manager.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "org.apache.bigtop.manager")
public class BigtopManagerAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigtopManagerAgentApplication.class, args);
    }

}
