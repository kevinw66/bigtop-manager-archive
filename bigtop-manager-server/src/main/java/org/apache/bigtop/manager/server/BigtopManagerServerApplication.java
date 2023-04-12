package org.apache.bigtop.manager.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.apache.bigtop.manager")
public class BigtopManagerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigtopManagerServerApplication.class, args);
    }

}
