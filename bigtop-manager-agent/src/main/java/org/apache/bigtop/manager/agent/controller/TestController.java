package org.apache.bigtop.manager.agent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.mpack.ServiceScriptManager;
import org.apache.bigtop.manager.spi.mpack.Script;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@Tag(name = "Test Controller")
@RestController
public class TestController {

    @Resource
    private ServiceScriptManager serviceScriptManager;


    @Operation(summary = "test", description = "test")
    @GetMapping(value = "/test")
    private String test() {
        Script script = serviceScriptManager.getScript("org.apache.bigtop.manager.mpack.zookeeper.ZookeeperServerScript");
        log.info("script: ", script);
        System.out.println("script: " + script);
        script.install();
        script.configuration();
//        script.start();
//        script.stop();
        return script.getName();
    }
}
