package org.apache.bigtop.manager.server.model.req.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class AbstractCommandReq {

    @Schema(example = "START")
    private String command;

    @Schema(example = "c1")
    private String clusterName;

    @Schema(example = "BIGTOP")
    private String stackName;

    @Schema(example = "3.2.0")
    private String stackVersion;

}
