package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.ServiceConfigDTO;
import org.apache.bigtop.manager.server.model.mapper.ConfigMapper;
import org.apache.bigtop.manager.server.model.req.ServiceConfigReq;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.model.vo.ServiceConfigVO;
import org.apache.bigtop.manager.server.service.ConfigService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Cluster Configuration Controller")
@Validated
@RestController
@RequestMapping("/clusters/{clusterId}/configurations")
public class ConfigController {

    @Resource
    private ConfigService configService;

    @Operation(summary = "list", description = "list all version configurations")
    @GetMapping
    public ResponseEntity<List<ServiceConfigVO>> list(@PathVariable Long clusterId) {
        return ResponseEntity.success(configService.list(clusterId));
    }

    @Operation(summary = "list", description = "list all latest configurations")
    @GetMapping("/latest")
    public ResponseEntity<List<ServiceConfigVO>> latest(@PathVariable Long clusterId) {
        return ResponseEntity.success(configService.latest(clusterId));
    }

    @Operation(summary = "update", description = "update|create|roll-back configurations")
    @PutMapping
    public ResponseEntity<CommandVO> update(@PathVariable Long clusterId,
                                            @RequestBody List<@Valid ServiceConfigReq> serviceConfigReqs) {
        List<ServiceConfigDTO> serviceConfigDTOList = ConfigMapper.INSTANCE.fromReq2DTO(serviceConfigReqs);
        log.info("serviceConfigDTOList: {}", serviceConfigDTOList);
        CommandVO commandVO = configService.update(clusterId, serviceConfigDTOList);
        return ResponseEntity.success(commandVO);
    }

}
