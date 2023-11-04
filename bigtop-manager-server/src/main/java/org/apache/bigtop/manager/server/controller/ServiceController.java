package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.ServiceVO;
import org.apache.bigtop.manager.server.service.ServiceService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Service Controller")
@RestController
@RequestMapping("/services")
public class ServiceController {

    @Resource
    private ServiceService serviceService;

    @Operation(summary = "list", description = "List services")
    @GetMapping
    public ResponseEntity<List<ServiceVO>> list() {
        return ResponseEntity.success(serviceService.list());
    }

    @Operation(summary = "get", description = "Get a service")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceVO> get(@PathVariable Long id) {
        return ResponseEntity.success(serviceService.get(id));
    }

    @Operation(summary = "get", description = "Get host-component list by service id")
    @GetMapping("/host-components/{id}")
    public ResponseEntity<List<HostComponentVO>> hostComponent(@PathVariable Long id) {
        return ResponseEntity.success(serviceService.hostComponent(id));
    }

}
