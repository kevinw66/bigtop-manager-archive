package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.ConfigurationDTO;
import org.apache.bigtop.manager.server.model.mapper.ConfigurationMapper;
import org.apache.bigtop.manager.server.model.req.ConfigurationReq;
import org.apache.bigtop.manager.server.model.vo.ConfigurationVO;
import org.apache.bigtop.manager.server.service.ConfigurationService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Cluster Configuration Controller")
@Validated
@RestController
@RequestMapping("/clusters/{clusterId}/configurations")
public class ConfigurationController {

    @Resource
    private ConfigurationService configurationService;

    @Operation(summary = "list", description = "list all version configurations")
    @GetMapping
    public ResponseEntity<List<ConfigurationVO>> list(@PathVariable Long clusterId) {
        List<ConfigurationVO> configurationVOList = configurationService.list(clusterId);
        return ResponseEntity.success(configurationVOList);
    }

    @Operation(summary = "list", description = "list all latest configurations")
    @GetMapping("/latest")
    public ResponseEntity<List<ConfigurationVO>> latest(@PathVariable Long clusterId) {
        List<ConfigurationVO> configurationVOList = configurationService.latest(clusterId);
        return ResponseEntity.success(configurationVOList);
    }

    @Operation(summary = "update", description = "update|create|roll-back configurations")
    @PutMapping
    public ResponseEntity<List<ConfigurationVO>> update(@PathVariable Long clusterId,
                                                        @RequestBody List<@Valid ConfigurationReq> configurationReqs) {
        List<ConfigurationDTO> configurationDTOList = ConfigurationMapper.INSTANCE.Request2DTO(configurationReqs);
        log.info("configurationDTOList: {}", configurationDTOList);
        List<ConfigurationVO> configurationVOList = configurationService.update(clusterId, configurationDTOList);
        return ResponseEntity.success(configurationVOList);
    }

}
