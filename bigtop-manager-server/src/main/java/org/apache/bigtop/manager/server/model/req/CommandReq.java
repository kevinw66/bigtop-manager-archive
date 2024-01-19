package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.config.CommandGroupSequenceProvider;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.model.req.command.ClusterCommandReq;
import org.apache.bigtop.manager.server.model.req.command.ComponentCommandReq;
import org.apache.bigtop.manager.server.model.req.command.HostCommandReq;
import org.apache.bigtop.manager.server.model.req.command.ServiceCommandReq;
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

    @Schema(example = "1")
    private Long clusterId;

    @NotNull
    @Schema(example = "cluster")
    private CommandLevel commandLevel;

    @Schema(description = "Command details for cluster level command")
    private ClusterCommandReq clusterCommand;

    @NotNull(groups = {CommandGroupSequenceProvider.HostCommandGroup.class})
    @Schema(description = "Command details for host level command")
    private List<HostCommandReq> hostCommands;

    @NotEmpty(groups = {CommandGroupSequenceProvider.ServiceCommandGroup.class})
    @Schema(description = "Command details for service level command")
    private List<@Valid ServiceCommandReq> serviceCommands;

    @NotNull(groups = {CommandGroupSequenceProvider.ComponentCommandGroup.class})
    @Schema(description = "Command details for component level command")
    private ComponentCommandReq componentCommands;

    // TODO need to delete this
    @Schema(description = "(deprecated) Command details for host level command")
    private HostCommandReq hostCommand;
}
