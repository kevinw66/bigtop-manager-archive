package org.apache.bigtop.manager.agent.stack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScriptHandlerTest {
    @Resource
    private ScriptHandler scriptHandler;

    @Test
    void scriptHandler() {
        Script script = scriptHandler.scriptHandler("ZookeeperServerScript");
        System.out.println(script);
        script.start();
//        script.configuration();
    }
}