package org.apache.bigtop.manager.agent.utils.template;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class XmlTemplateTest {
    @Resource
    private XmlTemplate xmlTemplate;

    @Test
    void writeProperties() {
        String filePath = "target/test.xml";
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user", "aaa");
        hashMap.put("usera", "aaa");
        xmlTemplate.writeXml(filePath, hashMap);
    }
}