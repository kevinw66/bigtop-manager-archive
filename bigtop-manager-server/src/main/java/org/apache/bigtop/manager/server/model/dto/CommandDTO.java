package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.model.dto.command.ClusterCommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.ComponentCommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.HostCommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.commons.text.CaseUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

@Data
public class CommandDTO implements Serializable {

    private Command command;

    private String customCommand;

    private Long clusterId;

    private CommandLevel commandLevel;

    private ClusterCommandDTO clusterCommand;

    private List<HostCommandDTO> hostCommands;

    private List<ServiceCommandDTO> serviceCommands;

    private ComponentCommandDTO componentCommands;

    private HostCommandDTO hostCommand;

    public String getContext() {
        if (command == null) {
            return MessageFormat.format("{0} for {1}", customCommand, commandLevel.toLowerCase());
        } else {
            return MessageFormat.format("{0} for {1}", CaseUtils.toCamelCase(command.name(), true), commandLevel.toLowerCase());
        }
    }

}
