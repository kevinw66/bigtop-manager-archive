package org.apache.bigtop.manager.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "org.apache.bigtop.manager")
public class BigtopManagerCommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigtopManagerCommonApplication.class, args);
    }

}
