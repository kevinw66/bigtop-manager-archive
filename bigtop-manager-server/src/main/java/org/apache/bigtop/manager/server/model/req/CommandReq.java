package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.config.CommandGroupSequenceProvider;
import org.apache.bigtop.manager.server.enums.CommandLevel;
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

    @NotNull
    @Schema(example = "1")
    private Long clusterId;

    @NotNull
    @Schema(example = "CLUSTER")
    private CommandLevel commandLevel;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ComponentCommandGroup.class, CommandGroupSequenceProvider.HostCommandGroup.class})
    @Schema(example = "[\"zookeeper_server\"]", description = "is required when CommandLevel is COMPONENT or HOST")
    private List<String> componentNames;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ComponentCommandGroup.class})
    @Schema(example = "zookeeper", description = "is required when CommandLevel is COMPONENT")
    private String serviceName;

    @NotEmpty(groups = {CommandGroupSequenceProvider.HostCommandGroup.class})
    @Schema(example = "node1", description = "is required when CommandLevel is HOST")
    private String hostname;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ServiceCommandGroup.class, CommandGroupSequenceProvider.ServiceInstallCommandGroup.class})
    @Schema(example = "[\"zookeeper\"]", description = "is required when CommandLevel is SERVICE or SERVICE and Command is INSTALL")
    private List<String> serviceNames;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ServiceInstallCommandGroup.class})
    @Schema(example = "{\"zookeeper_server\": [\"node1\"]}", description = "is required when CommandLevel is SERVICE and Command is INSTALL")
    private Map<String, Set<String>> componentHosts;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ServiceInstallCommandGroup.class})
    @Schema(description = "is required when CommandLevel is SERVICE and Command is INSTALL")
    private List<@Valid ConfigurationReq> serviceConfigs;
}
