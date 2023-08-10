package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.vo.ServiceVersionVO;
import org.apache.bigtop.manager.server.model.vo.StackRepoVO;
import org.apache.bigtop.manager.server.model.vo.StackVO;
import org.apache.bigtop.manager.server.service.StackService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "get", description = "Get a stack")
    @GetMapping("/{id}")
    public ResponseEntity<StackVO> get(@PathVariable Long id) {
        return ResponseEntity.success(stackService.get(id));
    }

    @Operation(summary = "get", description = "Get a stack service versions")
    @GetMapping("/versions/{stackName}/{stackVersion}")
    public ResponseEntity<List<ServiceVersionVO>> versions(@PathVariable String stackName, @PathVariable String stackVersion) {
        return ResponseEntity.success(stackService.versions(stackName, stackVersion));
    }

    @Operation(summary = "get", description = "Get a stack repos")
    @GetMapping("/repos/{stackName}/{stackVersion}")
    public ResponseEntity<List<StackRepoVO>> repos(@PathVariable String stackName, @PathVariable String stackVersion) {
        return ResponseEntity.success(stackService.repos(stackName, stackVersion));
    }

}
