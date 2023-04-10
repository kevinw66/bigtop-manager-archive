package org.apache.bigtop.manager.common.configuration;

import org.apache.bigtop.manager.common.BigtopManagerCommonApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = BigtopManagerCommonApplication.class)
class YamlConfigurationTest {
    @Resource
    Yaml yaml;

    @Test
    void testYaml() {
        Assertions.assertNotNull(yaml);
    }
}