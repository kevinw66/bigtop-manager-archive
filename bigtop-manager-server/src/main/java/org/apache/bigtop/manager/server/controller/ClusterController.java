package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.req.ClusterReq;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cluster Controller")
@RestController
@RequestMapping("/clusters")
public class ClusterController {

    @Resource
    private ClusterService clusterService;

    @Operation(summary = "list", description = "List clusters")
    @GetMapping
    public ResponseEntity<List<ClusterVO>> list() {
        return ResponseEntity.success(clusterService.list());
    }

    @Operation(summary = "get", description = "Get a cluster")
    @GetMapping("/{id}")
    public ResponseEntity<ClusterVO> get(@PathVariable Long id) {
        return ResponseEntity.success(clusterService.get(id));
    }

    @Operation(summary = "update", description = "Update a cluster")
    @PutMapping("/{id}")
    public ResponseEntity<ClusterVO> update(@PathVariable Long id, @RequestBody @Validated ClusterReq clusterReq) {
        ClusterDTO clusterDTO = ClusterMapper.INSTANCE.fromReq2DTO(clusterReq);
        return ResponseEntity.success(clusterService.update(id, clusterDTO));
    }

}
