package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StackReq {

    @Schema(example = "BIGTOP")
    private String stackName;

    @Schema(example = "3.3.0")
    private String stackVersion;
}
