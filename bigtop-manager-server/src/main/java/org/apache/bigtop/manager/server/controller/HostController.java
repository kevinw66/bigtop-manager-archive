package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.mapper.HostMapper;
import org.apache.bigtop.manager.server.model.request.HostRequest;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Host Controller")
@RestController
@RequestMapping("/hosts")
public class HostController {

    @Resource
    private HostService hostService;

    @Operation(summary = "list", description = "List hosts")
    @GetMapping
    private ResponseEntity<List<HostVO>> list() {
        return ResponseEntity.success(hostService.list());
    }

    @Operation(summary = "create", description = "Create a host")
    @PostMapping
    private ResponseEntity<HostVO> create(@RequestBody HostRequest hostRequest) {
        HostDTO hostDTO = HostMapper.INSTANCE.Request2DTO(hostRequest);
        return ResponseEntity.success(hostService.create(hostDTO));
    }

    @Operation(summary = "get", description = "Get a host")
    @GetMapping("/{id}")
    private ResponseEntity<HostVO> get(@PathVariable Long id) {
        return ResponseEntity.success(hostService.get(id));
    }

    @Operation(summary = "update", description = "Update a host")
    @PutMapping("/{id}")
    private ResponseEntity<HostVO> update(@PathVariable Long id, @RequestBody HostRequest hostRequest) {
        HostDTO hostDTO = HostMapper.INSTANCE.Request2DTO(hostRequest);
        return ResponseEntity.success(hostService.update(id, hostDTO));
    }

    @Operation(summary = "delete", description = "Delete a host")
    @DeleteMapping("/{id}")
    private ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.success(hostService.delete(id));
    }

    @Operation(summary = "cache", description = "distribute cache")
    @GetMapping("/cache")
    private void cache(@RequestParam Long clusterId) {
        hostService.cache(clusterId);
    }

}
