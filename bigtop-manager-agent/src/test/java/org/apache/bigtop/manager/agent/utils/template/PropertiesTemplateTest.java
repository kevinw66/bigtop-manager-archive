package org.apache.bigtop.manager.agent.utils.template;

import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.HashMap;

@SpringBootTest
class PropertiesTemplateTest {
    @Resource
    private PropertiesTemplate propertiesTemplate;

    @Test
    void writeProperties() throws TemplateException, IOException {
        String filePath = "target/test.properties";
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user", "aaa");
        hashMap.put("usera", "aaa");
        propertiesTemplate.writeProperties(filePath, hashMap);
    }
}