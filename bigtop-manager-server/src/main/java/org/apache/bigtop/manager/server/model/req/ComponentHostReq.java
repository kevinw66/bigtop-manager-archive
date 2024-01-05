package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ComponentHostReq {

    @NotNull
    @Schema(description = "Component name", example = "zookeeper-server")
    private String componentName;

    @NotEmpty
    @Schema(description = "Hostnames for component", example = "[\"node1\"]")
    private List<String> hostnames;
}
