package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class CommandDTO implements Serializable {

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

    private List<ConfigurationDTO> serviceConfigs;

    public String getContext() {
        return "CommandDTO{" +
                "command=" + command +
                ", customCommand='" + customCommand + '\'' +
                ", stackName='" + stackName + '\'' +
                ", stackVersion='" + stackVersion + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", commandType=" + commandType +
                ", componentNames=" + componentNames +
                ", serviceName='" + serviceName + '\'' +
                ", hostname='" + hostname + '\'' +
                ", serviceNames=" + serviceNames +
                ", componentHosts=" + componentHosts +
                '}';
    }

}
