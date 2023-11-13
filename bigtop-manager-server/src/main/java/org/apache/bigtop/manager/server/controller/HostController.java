package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.mapper.HostMapper;
import org.apache.bigtop.manager.server.model.req.HostReq;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.model.vo.PageVO;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
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
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "pageNum", schema = @Schema(type = "integer", defaultValue = "1")),
            @Parameter(in = ParameterIn.QUERY, name = "pageSize", schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(in = ParameterIn.QUERY, name = "orderBy", schema = @Schema(type = "string", defaultValue = "id")),
            @Parameter(in = ParameterIn.QUERY, name = "sort", description = "asc/desc", schema = @Schema(type = "string", defaultValue = "asc"))
    })
    @GetMapping
    public ResponseEntity<PageVO<HostVO>> list(@PathVariable Long clusterId) {
        return ResponseEntity.success(hostService.list(clusterId));
    }

    @Operation(summary = "create", description = "Create a host")
    @PostMapping
    public ResponseEntity<CommandVO> create(@PathVariable Long clusterId, @RequestBody List<String> hostnames) {
        return ResponseEntity.success(hostService.create(clusterId, hostnames));
    }

    @Operation(summary = "get", description = "Get a host")
//    @GetMapping("/{id}")
    public ResponseEntity<HostVO> get(@PathVariable Long id) {
        return ResponseEntity.success(hostService.get(id));
    }

    @Operation(summary = "update", description = "Update a host")
//    @PutMapping("/{id}")
    public ResponseEntity<HostVO> update(@PathVariable Long id, @RequestBody @Validated HostReq hostReq) {
        HostDTO hostDTO = HostMapper.INSTANCE.Req2DTO(hostReq);
        return ResponseEntity.success(hostService.update(id, hostDTO));
    }

    @Operation(summary = "delete", description = "Delete a host")
//    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.success(hostService.delete(id));
    }

    @Operation(summary = "cache", description = "distribute cache")
    @GetMapping("/cache")
    public Boolean cache(@PathVariable Long clusterId) {
        return hostService.cache(clusterId);
    }

}
