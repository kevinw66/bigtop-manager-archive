package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.ConfigurationDTO;
import org.apache.bigtop.manager.server.model.mapper.ConfigurationMapper;
import org.apache.bigtop.manager.server.model.req.ConfigurationReq;
import org.apache.bigtop.manager.server.model.vo.ConfigurationVO;
import org.apache.bigtop.manager.server.service.ConfigurationService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Configuration Controller")
@RestController
@RequestMapping("/configurations")
public class ConfigurationController {

    @Resource
    private ConfigurationService configurationService;

    @Operation(summary = "list", description = "list all version configurations")
    @GetMapping("/{clusterName}")
    public ResponseEntity<List<ConfigurationVO>> list(@PathVariable String clusterName) {
        List<ConfigurationVO> configurationVOList = configurationService.list(clusterName);
        return ResponseEntity.success(configurationVOList);
    }

    @Operation(summary = "list", description = "list all latest configurations")
    @GetMapping("/{clusterName}/latest")
    public ResponseEntity<List<ConfigurationVO>> latest(@PathVariable String clusterName) {
        List<ConfigurationVO> configurationVOList = configurationService.latest(clusterName);
        return ResponseEntity.success(configurationVOList);
    }

    @Operation(summary = "update", description = "update|create|roll-back configurations")
    @PutMapping("/{clusterName}")
    public ResponseEntity<List<ConfigurationVO>> update(@PathVariable String clusterName,
                                                        @RequestBody List<ConfigurationReq> configurationReqs) {
        List<ConfigurationDTO> configurationDTOList = ConfigurationMapper.INSTANCE.Request2DTO(configurationReqs);
        log.info("configurationDTOList: {}", configurationDTOList);
        List<ConfigurationVO> configurationVOList = configurationService.update(clusterName, configurationDTOList);
        return ResponseEntity.success(configurationVOList);
    }

}
