package org.apache.bigtop.manager.server;

import org.apache.bigtop.manager.common.aot.BigtopManagerRuntimeHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication(scanBasePackages = "org.apache.bigtop.manager")
@ImportRuntimeHints(BigtopManagerRuntimeHints.class)
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
