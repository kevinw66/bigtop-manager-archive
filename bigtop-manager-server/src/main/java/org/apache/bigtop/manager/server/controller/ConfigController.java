package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.vo.ServiceConfigVO;
import org.apache.bigtop.manager.server.service.ConfigService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Cluster Configuration Controller")
@Validated
@RestController
@RequestMapping("/clusters/{clusterId}/configurations")
public class ConfigController {

    @Resource
    private ConfigService configService;

    @Operation(summary = "list", description = "list all version configurations")
    @GetMapping
    public ResponseEntity<List<ServiceConfigVO>> list(@PathVariable Long clusterId) {
        return ResponseEntity.success(configService.list(clusterId));
    }

    @Operation(summary = "list", description = "list all latest configurations")
    @GetMapping("/latest")
    public ResponseEntity<List<ServiceConfigVO>> latest(@PathVariable Long clusterId) {
        return ResponseEntity.success(configService.latest(clusterId));
    }
}
