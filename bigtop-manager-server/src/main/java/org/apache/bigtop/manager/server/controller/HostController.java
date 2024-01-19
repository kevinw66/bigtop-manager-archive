package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.mapper.HostMapper;
import org.apache.bigtop.manager.server.model.req.HostReq;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cluster Host Controller")
@RestController
@RequestMapping("/clusters/{clusterId}/hosts")
public class HostController {

    @Resource
    private HostService hostService;

    @Operation(summary = "list", description = "List hosts")
    @GetMapping
    public ResponseEntity<List<HostVO>> list(@PathVariable Long clusterId) {
        return ResponseEntity.success(hostService.list(clusterId));
    }

    @Operation(summary = "get", description = "Get a host")
//    @GetMapping("/{id}")
    public ResponseEntity<HostVO> get(@PathVariable Long id) {
        return ResponseEntity.success(hostService.get(id));
    }

    @Operation(summary = "update", description = "Update a host")
//    @PutMapping("/{id}")
    public ResponseEntity<HostVO> update(@PathVariable Long id, @RequestBody @Validated HostReq hostReq) {
        HostDTO hostDTO = HostMapper.INSTANCE.fromReq2DTO(hostReq);
        return ResponseEntity.success(hostService.update(id, hostDTO));
    }

    @Operation(summary = "delete", description = "Delete a host")
//    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.success(hostService.delete(id));
    }

}
