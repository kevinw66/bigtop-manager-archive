package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HostReq {

    @NotNull
    @Schema(example = "1")
    private Long clusterId;

    @NotEmpty
    @Schema(example = "host1")
    private String hostname;
}
