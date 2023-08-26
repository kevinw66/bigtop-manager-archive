package org.apache.bigtop.manager.server.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;
import org.apache.bigtop.manager.server.enums.CommandType;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class CommandRequest {

    @Schema(example = "START")
    private String command;

    @Schema(example = "c1")
    private String clusterName;

    @Schema(example = "BIGTOP")
    private String stackName;

    @Schema(example = "3.2.0")
    private String stackVersion;

    private List<String> componentNames;

    private String serviceName;

    private String hostname;

    private List<String> serviceNames;

    private Map<String, Set<String>> componentHosts;

    private CommandType commandType;

}
