package org.apache.bigtop.manager.server.model.req.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ComponentCommandReq {

    @NotBlank
    @Schema(description = "Component Name", example = "zookeeper_server")
    private String componentName;

    @NotEmpty
    @Schema(description = "Host Name List", example = "[bigtop-manager-server, bigtop-manager-agent]")
    private List<String> hostnames;
}
