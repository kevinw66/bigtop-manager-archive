package org.apache.bigtop.manager.server.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ClusterRequest {

    @Schema(example = "c1")
    private String clusterName;

    @Schema(example = "1")
    private Integer clusterType;
}
