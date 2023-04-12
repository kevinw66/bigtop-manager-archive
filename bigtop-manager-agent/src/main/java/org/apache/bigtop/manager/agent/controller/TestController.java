package org.apache.bigtop.manager.agent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.bigtop.manager.agent.stack.Script;
import org.apache.bigtop.manager.agent.stack.ScriptHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "Test Controller")
@RestController
public class TestController {

    @Resource
    private ScriptHandler scriptHandler;


    @Operation(summary = "test", description = "test")
    @GetMapping(value = "/test")
    private String test() {
        Script script = scriptHandler.scriptHandler("ZookeeperServerScript");
        System.out.println(script);

        script.start();

        return script.getClass().getName();
    }
}
