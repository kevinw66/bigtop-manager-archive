package org.apache.bigtop.manager.server.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HostRequest {

    @Schema(example = "1")
    private Long clusterId;

    @Schema(example = "host1")
    private String hostname;
}
