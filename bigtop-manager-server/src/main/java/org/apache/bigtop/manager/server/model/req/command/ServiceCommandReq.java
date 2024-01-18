package org.apache.bigtop.manager.server.model.req.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.bigtop.manager.server.config.CommandGroupSequenceProvider;
import org.apache.bigtop.manager.server.model.req.ComponentHostReq;
import org.apache.bigtop.manager.server.model.req.TypeConfigReq;

import java.util.List;

@Data
public class ServiceCommandReq {

    @NotNull
    @Schema(description = "Service name", example = "zookeeper")
    private String serviceName;

    @Schema(description = "Config Description", example = "Initial config for zookeeper")
    private String configDesc;

    @Schema(description = "Config version", example = "1")
    private Integer version;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ServiceInstallCommandGroup.class})
    @Schema(description = "Components for service on each hosts")
    private List<@Valid ComponentHostReq> componentHosts;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ServiceInstallCommandGroup.class})
    @Schema(description = "Configs for service")
    private List<@Valid TypeConfigReq> configs;
}
