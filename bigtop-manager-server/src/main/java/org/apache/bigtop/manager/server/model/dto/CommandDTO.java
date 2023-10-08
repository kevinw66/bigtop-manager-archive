package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;
import lombok.NonNull;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandType;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class CommandDTO {

    @NonNull
    private Command command;

    private String customCommand;

    @NonNull
    private String stackName;

    @NonNull
    private String stackVersion;

    @NonNull
    private String clusterName;

    private List<String> componentNames;

    private String serviceName;

    private String hostname;

    private List<String> serviceNames;

    private Map<String, Set<String>> componentHosts;

    private CommandType commandType;
}
