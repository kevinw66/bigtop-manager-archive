package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

@Data
public class CommandDTO implements Serializable {

    private Command command;

    private String customCommand;

    private Long clusterId;

    private CommandLevel commandLevel;

    private List<String> componentNames;

    private String hostname;

    private List<ServiceCommandDTO> serviceCommands;

    public String getContext() {
        return MessageFormat.format("command={0}, customCommand={1}, clusterId={2}, commandLevel={3}",
                command, customCommand, clusterId, commandLevel);
    }

}
