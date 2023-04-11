package org.apache.bigtop.manager.agent.stack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScriptHandlerTest {
    @Resource
    ScriptHandler scriptHandler;

    @Test
    void scriptHandler() {
        Script zookeeperServerScript = scriptHandler.scriptHandler("ZookeeperServerScript");
        System.out.println(zookeeperServerScript);
    }
}