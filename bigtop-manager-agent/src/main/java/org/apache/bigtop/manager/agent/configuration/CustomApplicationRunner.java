package org.apache.bigtop.manager.agent.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.mpack.ServiceScriptManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class CustomApplicationRunner implements ApplicationRunner {
    @Resource
    private ServiceScriptManager serviceScriptManager;

    public LinkedBlockingQueue actionQueue;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("init start");
        serviceScriptManager.initScripts();
        actionQueue = new LinkedBlockingQueue();
        log.info("init end");
    }
}
