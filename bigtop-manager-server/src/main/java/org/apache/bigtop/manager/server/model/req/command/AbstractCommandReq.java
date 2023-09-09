package org.apache.bigtop.manager.server.model.req.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public abstract class AbstractCommandReq {

    @NotEmpty
    @Schema(example = "START")
    private String command;

    @NotEmpty
    @Schema(example = "c1")
    private String clusterName;

    @NotEmpty
    @Schema(example = "BIGTOP")
    private String stackName;

    @NotEmpty
    @Schema(example = "3.2.0")
    private String stackVersion;

}
