package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.mapper.CommandMapper;
import org.apache.bigtop.manager.server.model.req.CommandReq;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.service.CommandService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Command Controller")
@RestController
@RequestMapping("/command")
public class CommandController {

    @Resource
    private CommandService commandService;

    @Operation(summary = "command", description = "Command for component by [host,component,service,cluster]")
    @PostMapping
    public ResponseEntity<CommandVO> command(@RequestBody @Validated CommandReq commandReq) {
        CommandDTO commandDTO = CommandMapper.INSTANCE.Req2DTO(commandReq);
        CommandVO commandVO = commandService.command(commandDTO);
        return ResponseEntity.success(commandVO);
    }

}
