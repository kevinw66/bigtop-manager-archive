package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.mapper.CommandMapper;
import org.apache.bigtop.manager.server.model.req.ClusterReq;
import org.apache.bigtop.manager.server.model.req.command.ClusterCommandReq;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
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

    @Operation(summary = "create", description = "Create a cluster")
    @PostMapping
    public ResponseEntity<ClusterVO> create(@RequestBody ClusterReq clusterReq) {
        ClusterDTO clusterDTO = ClusterMapper.INSTANCE.Req2DTO(clusterReq);
        return ResponseEntity.success(clusterService.create(clusterDTO));
    }

    @Operation(summary = "get", description = "Get a cluster")
    @GetMapping("/{id}")
    public ResponseEntity<ClusterVO> get(@PathVariable Long id) {
        return ResponseEntity.success(clusterService.get(id));
    }

    @Operation(summary = "update", description = "Update a cluster")
    @PutMapping("/{id}")
    public ResponseEntity<ClusterVO> update(@PathVariable Long id, @RequestBody ClusterReq clusterReq) {
        ClusterDTO clusterDTO = ClusterMapper.INSTANCE.Req2DTO(clusterReq);
        return ResponseEntity.success(clusterService.update(id, clusterDTO));
    }

    @Operation(summary = "delete", description = "Delete a cluster")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.success(clusterService.delete(id));
    }

    @Operation(summary = "cluster command", description = "Command for cluster, only support [START|STOP|RESTART]")
    @PostMapping("/command")
    public ResponseEntity<CommandVO> command(@RequestBody ClusterCommandReq commandReq) {
        CommandDTO commandDTO = CommandMapper.INSTANCE.Req2DTO(commandReq);
        CommandVO commandVO = clusterService.command(commandDTO);
        return ResponseEntity.success(commandVO);
    }

}
