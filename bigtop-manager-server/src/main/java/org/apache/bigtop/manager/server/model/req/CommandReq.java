package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.config.CommandGroupSequenceProvider;
import org.apache.bigtop.manager.server.enums.CommandType;
import org.hibernate.validator.group.GroupSequenceProvider;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@GroupSequenceProvider(CommandGroupSequenceProvider.class)
public class CommandReq {

    @NotNull
    @Schema(example = "START")
    private Command command;

    @Schema(example = "custom_command")
    private String customCommand;

    @NotEmpty
    @Schema(example = "c1")
    private String clusterName;

    @NotEmpty
    @Schema(example = "bigtop")
    private String stackName;

    @NotEmpty
    @Schema(example = "3.3.0")
    private String stackVersion;

    @NotNull
    @Schema(example = "CLUSTER")
    private CommandType commandType;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ComponentCommandGroup.class, CommandGroupSequenceProvider.HostCommandGroup.class, CommandGroupSequenceProvider.HostInstallCommandGroup.class})
    @Schema(example = "[\"ZOOKEEPER_SERVER\"]", description = "is required when commandType is COMPONENT or HOST or HOST_INSTALL")
    private List<String> componentNames;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ComponentCommandGroup.class})
    @Schema(example = "ZOOKEEPER", description = "is required when commandType is COMPONENT")
    private String serviceName;

    @NotEmpty(groups = {CommandGroupSequenceProvider.HostCommandGroup.class, CommandGroupSequenceProvider.HostInstallCommandGroup.class})
    @Schema(example = "node1", description = "is required when commandType is HOST or HOST_INSTALL")
    private String hostname;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ServiceCommandGroup.class, CommandGroupSequenceProvider.ServiceInstallCommandGroup.class})
    @Schema(example = "[\"ZOOKEEPER\"]", description = "is required when commandType is SERVICE or SERVICE_INSTALL")
    private List<String> serviceNames;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ServiceInstallCommandGroup.class})
    @Schema(example = "{\"ZOOKEEPER_SERVER\": [\"node1\"]}", description = "is required when commandType is SERVICE_INSTALL")
    private Map<String, Set<String>> componentHosts;

    @Schema(description = "is optional when commandType is SERVICE_INSTALL")
    private List<@Valid ConfigurationReq> serviceConfigs;
}
