package org.apache.bigtop.manager.agent.stack.env;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class StackEnvironmentImplTest {

    @Resource
    private StackEnvImpl stackEnv;

    @Test
    void initEnv() {
        stackEnv.initEnv();
    }
}