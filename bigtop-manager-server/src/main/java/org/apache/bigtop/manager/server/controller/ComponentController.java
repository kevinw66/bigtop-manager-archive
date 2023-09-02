package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.mapper.CommandMapper;
import org.apache.bigtop.manager.server.model.req.command.ComponentCommandReq;
import org.apache.bigtop.manager.server.model.vo.ComponentVO;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.service.ComponentService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Component Controller")
@RestController
@RequestMapping("/components")
public class ComponentController {

    @Resource
    private ComponentService componentService;

    @Operation(summary = "list", description = "List components")
    @GetMapping
    public ResponseEntity<List<ComponentVO>> list() {
        return ResponseEntity.success(componentService.list());
    }

    @Operation(summary = "get", description = "Get a component")
    @GetMapping("/{id}")
    public ResponseEntity<ComponentVO> get(@PathVariable Long id) {
        return ResponseEntity.success(componentService.get(id));
    }

    @Operation(summary = "get", description = "Get host-component list by component id")
    @GetMapping("/host-components/{id}")
    public ResponseEntity<List<HostComponentVO>> hostComponent(@PathVariable Long id) {
        return ResponseEntity.success(componentService.hostComponent(id));
    }

    @Operation(summary = "component command", description = "command for component, only support [START|STOP|RESTART]")
    @PostMapping("/command")
    public ResponseEntity<CommandVO> command(@RequestBody ComponentCommandReq commandReq) {
        CommandDTO commandDTO = CommandMapper.INSTANCE.Req2DTO(commandReq);
        CommandVO commandVO = componentService.command(commandDTO);
        return ResponseEntity.success(commandVO);
    }

}
