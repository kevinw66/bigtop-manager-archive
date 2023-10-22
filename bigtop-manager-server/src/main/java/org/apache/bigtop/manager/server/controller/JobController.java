package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.model.vo.JobVO;
import org.apache.bigtop.manager.server.service.JobService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Job Controller")
@RestController
@RequestMapping("/jobs")
public class JobController {

    @Resource
    private JobService jobService;

    @Operation(summary = "list", description = "List clusters")
    @GetMapping
    public ResponseEntity<List<JobVO>> list() {
        return ResponseEntity.success(jobService.list());
    }

    @Operation(summary = "get", description = "Get a job")
    @GetMapping("/{id}")
    public ResponseEntity<JobVO> get(@PathVariable Long id) {
        return ResponseEntity.success(jobService.get(id));
    }
}
