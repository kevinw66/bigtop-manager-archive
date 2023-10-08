package org.apache.bigtop.manager.server.model.req.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;

@Data
public abstract class AbstractCommandReq {

    @NotNull
    @Schema(example = "START")
    private Command command;

    @Schema(example = "AAA")
    private String customCommand;

    @NotEmpty
    @Schema(example = "c1")
    private String clusterName;

    @NotEmpty
    @Schema(example = "BIGTOP")
    private String stackName;

    @NotEmpty
    @Schema(example = "3.3.0")
    private String stackVersion;

}
