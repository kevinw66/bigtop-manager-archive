package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.request.ClusterRequest;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "Cluster Controller")
@RestController
@RequestMapping("/clusters")
public class ClusterController {

    @Resource
    private ClusterService clusterService;

    @Operation(summary = "list", description = "List clusters")
    @GetMapping
    private ResponseEntity<List<ClusterVO>> list() {
        return ResponseEntity.success(clusterService.list());
    }

    @Operation(summary = "create", description = "Create a cluster")
    @PostMapping
    private ResponseEntity<ClusterVO> create(@RequestBody ClusterRequest clusterRequest) {
        ClusterDTO clusterDTO = new ClusterDTO();
        BeanUtils.copyProperties(clusterRequest, clusterDTO);
        return ResponseEntity.success(clusterService.create(clusterDTO));
    }

    @Operation(summary = "get", description = "Get a cluster")
    @GetMapping("/{id}")
    private ResponseEntity<ClusterVO> get(@PathVariable Long id) {
        return ResponseEntity.success(clusterService.get(id));
    }

    @Operation(summary = "update", description = "Update a cluster")
    @PutMapping("/{id}")
    private ResponseEntity<ClusterVO> update(@PathVariable Long id, @RequestBody ClusterRequest clusterRequest) {
        ClusterDTO clusterDTO = new ClusterDTO();
        BeanUtils.copyProperties(clusterRequest, clusterDTO);
        return ResponseEntity.success(clusterService.update(id, clusterDTO));
    }

    @Operation(summary = "delete", description = "Delete a cluster")
    @DeleteMapping("/{id}")
    private ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.success(clusterService.delete(id));
    }
}
