package org.apache.bigtop.manager.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.service.MonitoringService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Monitoring Controller")
@RestController
@RequestMapping("monitoring")
public class MonitoringController {

    @Resource
    MonitoringService monitoringService;

    @Operation(summary = "agent healthy", description = "agent healthy check")
    @GetMapping("agenthealthy")
    public ResponseEntity<JsonNode> agentHostsHealthyStatus() {
        return ResponseEntity.success(monitoringService.queryAgentsHealthyStatus());
    }

}
