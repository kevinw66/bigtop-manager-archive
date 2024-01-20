package org.apache.bigtop.manager.server.model.req.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HostCommandReq {

    @NotBlank
    @Schema(description = "Host Name", example = "bigtop-manager-server")
    private String hostname;
}
