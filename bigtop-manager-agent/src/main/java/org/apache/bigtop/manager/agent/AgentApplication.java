package org.apache.bigtop.manager.agent;

import org.apache.bigtop.manager.common.aot.BigtopManagerRuntimeHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication(scanBasePackages = "org.apache.bigtop.manager")
@ImportRuntimeHints(BigtopManagerRuntimeHints.class)
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }

}
