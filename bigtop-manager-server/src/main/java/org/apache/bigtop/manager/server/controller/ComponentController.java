package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.mapper.CommandMapper;
import org.apache.bigtop.manager.server.model.request.CommandRequest;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.service.ComponentService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Component Controller")
@RestController
@RequestMapping("/components")
public class ComponentController {

    @Resource
    private ComponentService componentService;

    @Operation(summary = "component command", description = "command for component, only support [START|STOP|RESTART]")
    @PostMapping("/command")
    public ResponseEntity<CommandVO> componentCommand(@RequestBody CommandRequest commandRequest) {
        CommandDTO commandDTO = CommandMapper.INSTANCE.Request2DTO(commandRequest);
        CommandVO commandVO = componentService.command(commandDTO);
        return ResponseEntity.success(commandVO);
    }

}
