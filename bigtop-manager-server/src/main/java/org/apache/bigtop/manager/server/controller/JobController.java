package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.vo.JobVO;
import org.apache.bigtop.manager.server.model.vo.PageVO;
import org.apache.bigtop.manager.server.service.JobService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Job Controller")
@RestController
@RequestMapping("/jobs")
public class JobController {

    @Resource
    private JobService jobService;

    @Operation(summary = "list", description = "List jobs")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "pageNum", schema = @Schema(type = "integer", defaultValue = "1")),
            @Parameter(in = ParameterIn.QUERY, name = "pageSize", schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(in = ParameterIn.QUERY, name = "orderBy", schema = @Schema(type = "string", defaultValue = "id")),
            @Parameter(in = ParameterIn.QUERY, name = "sort", description = "asc/desc", schema = @Schema(type = "string", defaultValue = "desc"))
    })
    @GetMapping
    public ResponseEntity<PageVO<JobVO>> list() {
        return ResponseEntity.success(jobService.list());
    }

    @Operation(summary = "get", description = "Get a job")
    @GetMapping("/{id}")
    public ResponseEntity<JobVO> get(@PathVariable Long id) {
        return ResponseEntity.success(jobService.get(id));
    }
}
