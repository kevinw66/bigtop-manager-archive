package org.apache.bigtop.manager.server.model.req.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class HostCommandReq {

    @NotEmpty
    @Schema(description = "Component name", example = "[zookeeper_server]")
    private List<String> componentNames;

    @NotBlank
    @Schema(description = "Host Name", example = "bigtop-manager-server")
    private String hostname;
}
