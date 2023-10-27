package org.apache.bigtop.manager.server.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandType;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommandEvent extends Event {

    private Long jobId;

    private Command command;

    private String customCommand;

    private String stackName;

    private String stackVersion;

    private String clusterName;

    private List<String> componentNames;

    private String serviceName;

    private String hostname;

    private List<String> serviceNames;

    private Map<String, Set<String>> componentHosts;

    private CommandType commandType;
}
