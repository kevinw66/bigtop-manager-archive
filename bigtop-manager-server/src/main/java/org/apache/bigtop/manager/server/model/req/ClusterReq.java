package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ClusterReq {

    @NotEmpty
    @Schema(example = "c1")
    private String clusterName;

    @NotNull
    @Schema(example = "1")
    private Integer clusterType;

    @NotEmpty
    @Schema(example = "BIGTOP")
    private String stackName;

    @NotEmpty
    @Schema(example = "3.3.0")
    private String stackVersion;

    private List<RepoReq> repoInfoList;

}
