package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.mapper.CommandMapper;
import org.apache.bigtop.manager.server.model.request.CommandRequest;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.service.ServiceService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Service Controller")
@RestController
@RequestMapping("/services")
public class ServiceController {

    @Resource
    private ServiceService serviceService;

    @Operation(summary = "service command", description = "Command for service, only support [START|STOP|RESTART|INSTALL]")
    @PostMapping("/command")
    public ResponseEntity<CommandVO> command(@RequestBody CommandRequest commandRequest) {
        CommandDTO commandDTO = CommandMapper.INSTANCE.Request2DTO(commandRequest);
        CommandVO commandVO = serviceService.command(commandDTO);
        return ResponseEntity.success(commandVO);
    }

}
