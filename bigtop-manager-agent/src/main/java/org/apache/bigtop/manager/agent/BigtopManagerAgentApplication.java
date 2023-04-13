package org.apache.bigtop.manager.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.apache.bigtop.manager")
public class BigtopManagerAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigtopManagerAgentApplication.class, args);
    }

}
