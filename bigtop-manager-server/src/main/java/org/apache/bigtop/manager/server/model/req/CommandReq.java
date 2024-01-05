package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.config.CommandGroupSequenceProvider;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.hibernate.validator.group.GroupSequenceProvider;

import java.util.List;

@Data
@GroupSequenceProvider(CommandGroupSequenceProvider.class)
public class CommandReq {

    @NotNull
    @Schema(example = "start")
    private Command command;

    @Schema(example = "custom_command")
    private String customCommand;

    @NotNull
    @Schema(example = "1")
    private Long clusterId;

    @NotNull
    @Schema(example = "cluster")
    private CommandLevel commandLevel;

//    @NotEmpty(groups = {CommandGroupSequenceProvider.ComponentCommandGroup.class, CommandGroupSequenceProvider.HostCommandGroup.class})
    @Schema(example = "[\"zookeeper_server\"]", description = "is required when CommandLevel is COMPONENT or HOST")
    private List<String> componentNames;

//    @NotEmpty(groups = {CommandGroupSequenceProvider.HostCommandGroup.class})
    @Schema(example = "node1", description = "is required when CommandLevel is HOST")
    private String hostname;

    @Schema(description = "Command details for service level command")
    private List<@Valid ServiceCommandReq> serviceCommands;
}
