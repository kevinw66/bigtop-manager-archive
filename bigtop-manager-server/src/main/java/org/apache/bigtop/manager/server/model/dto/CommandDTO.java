package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandType;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class CommandDTO {

    private Command command;

    private String customCommand;

    private String stackName;

    private String stackVersion;

    private String clusterName;

    private CommandType commandType;

    private List<String> componentNames;

    private String serviceName;

    private String hostname;

    private List<String> serviceNames;

    private Map<String, Set<String>> componentHosts;

}
