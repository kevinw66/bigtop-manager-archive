package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.service.HostComponentService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Cluster Host-Component Controller")
@RestController
@RequestMapping("/clusters/{clusterId}/host-components")
public class HostComponentController {

    @Resource
    private HostComponentService hostComponentService;

    @Operation(summary = "list", description = "List host-components")
    @GetMapping
    public ResponseEntity<List<HostComponentVO>> list(@PathVariable Long clusterId) {
        return ResponseEntity.success(hostComponentService.list(clusterId));
    }

    @Operation(summary = "list", description = "List host-components")
    @GetMapping("/hosts/{hostId}")
    public ResponseEntity<List<HostComponentVO>> listByHost(@PathVariable Long clusterId, @PathVariable Long hostId) {
        return ResponseEntity.success(hostComponentService.listByHost(clusterId, hostId));
    }

    @Operation(summary = "list", description = "List host-components")
    @GetMapping("/services/{serviceId}")
    public ResponseEntity<List<HostComponentVO>> listByService(@PathVariable Long clusterId, @PathVariable Long serviceId) {
        return ResponseEntity.success(hostComponentService.listByService(clusterId, serviceId));
    }

}
