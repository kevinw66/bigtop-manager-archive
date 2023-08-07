package org.apache.bigtop.manager.server.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.bigtop.manager.common.pojo.stack.RepoInfo;

import java.util.List;

@Data
public class ClusterRequest {

    @Schema(example = "c1")
    private String clusterName;

    @Schema(example = "1")
    private Integer clusterType;

    @Schema(example = "BIGTOP")
    private String stackName;

    @Schema(example = "3.2.0")
    private String stackVersion;

    private List<RepoInfo> repoInfoList;

}
