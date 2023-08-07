package org.apache.bigtop.manager.server.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StackRequest {

    @Schema(example = "BIGTOP")
    private String stackName;

    @Schema(example = "3.2.0")
    private String stackVersion;
}
