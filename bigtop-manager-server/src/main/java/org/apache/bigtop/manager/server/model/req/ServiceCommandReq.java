package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ServiceCommandReq {

    @NotNull
    @Schema(description = "Service name", example = "zookeeper")
    private String serviceName;

    @Schema(description = "Components for service on each hosts")
    private List<@Valid ComponentHostReq> componentHosts;

    @Schema(description = "Configs for service")
    private List<@Valid ConfigDataReq> configs;
}
