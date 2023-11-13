package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.vo.ComponentVO;
import org.apache.bigtop.manager.server.service.ComponentService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Cluster Component Controller")
@RestController
@RequestMapping("/clusters/{clusterId}/components")
public class ComponentController {

    @Resource
    private ComponentService componentService;

    @Operation(summary = "list", description = "List components")
    @GetMapping
    public ResponseEntity<List<ComponentVO>> list(@PathVariable Long clusterId) {
        return ResponseEntity.success(componentService.list(clusterId));
    }

    @Operation(summary = "get", description = "Get a component")
    @GetMapping("/{id}")
    public ResponseEntity<ComponentVO> get(@PathVariable Long id) {
        return ResponseEntity.success(componentService.get(id));
    }

}
