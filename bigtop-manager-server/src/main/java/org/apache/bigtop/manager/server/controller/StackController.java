package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.vo.StackVO;
import org.apache.bigtop.manager.server.service.StackService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Stack Controller")
@RestController
@RequestMapping("/stacks")
public class StackController {

    @Resource
    private StackService stackService;

    @Operation(summary = "list", description = "List stacks")
    @GetMapping
    public ResponseEntity<List<StackVO>> list() {
        return ResponseEntity.success(stackService.list());
    }
}
