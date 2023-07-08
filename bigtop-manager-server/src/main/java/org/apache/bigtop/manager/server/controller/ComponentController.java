package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.service.ComponentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@Tag(name = "Component Controller")
@RestController
@RequestMapping("/clusters")
public class ComponentController {

    @Resource
    private ComponentService componentService;

    @Operation(summary = "handleComponent", description = "action for component, only support start/stop/restart/status/configuration")
    @GetMapping("/{clusterName}/components/{componentName}/{action}")
    private void handleComponent(@PathVariable String clusterName,
                                 @PathVariable String componentName,
                                 @PathVariable String action) {

        componentService.handleComponent(clusterName, componentName, action);
    }

}
