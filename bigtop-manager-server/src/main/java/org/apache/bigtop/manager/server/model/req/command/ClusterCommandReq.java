package org.apache.bigtop.manager.server.model.req.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.bigtop.manager.server.model.req.RepoReq;

import java.util.List;

@Data
public class ClusterCommandReq {

    @NotEmpty
    @Schema(example = "c1")
    private String clusterName;

    @NotNull
    @Schema(example = "1")
    private Integer clusterType;

    @NotEmpty
    @Schema(example = "bigtop")
    private String stackName;

    @NotEmpty
    @Schema(example = "3.3.0")
    private String stackVersion;

    @NotEmpty
    private List<RepoReq> repoInfoList;

    @NotEmpty
    private List<String> hostnames;
}
