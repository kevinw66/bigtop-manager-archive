package org.apache.bigtop.manager.server.command.stage.factory;

import lombok.Data;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.model.dto.RepoDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.command.ClusterCommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.HostCommandDTO;

import java.util.List;

@Data
public class StageContext {

    private StageType stageType;

    private Long clusterId;

    private String clusterName;

    private String stackName;

    private String stackVersion;

    private List<String> hostnames;

    private ServiceDTO serviceDTO;

    private ComponentDTO componentDTO;

    private List<RepoDTO> repoInfoList;

    public static StageContext fromPayload(String payload) {
        StageContext context = new StageContext();
        CommandDTO commandDTO = JsonUtils.readFromString(payload, CommandDTO.class);

        context.setClusterId(commandDTO.getClusterId());

        switch (commandDTO.getCommandLevel()) {
            case CLUSTER -> fromClusterCommandPayload(context, commandDTO);
            case HOST -> fromHostCommandPayload(context, commandDTO);
            case SERVICE -> fromServiceCommandPayload(context, commandDTO);
            case COMPONENT -> fromComponentCommandPayload(context, commandDTO);
        }

        return context;
    }

    private static void fromClusterCommandPayload(StageContext context, CommandDTO commandDTO) {
        ClusterCommandDTO clusterCommand = commandDTO.getClusterCommand();

        context.setClusterName(clusterCommand.getClusterName());
        context.setStackName(clusterCommand.getStackName());
        context.setStackVersion(clusterCommand.getStackVersion());
        context.setHostnames(clusterCommand.getHostnames());
        context.setRepoInfoList(clusterCommand.getRepoInfoList());
    }

    private static void fromHostCommandPayload(StageContext context, CommandDTO commandDTO) {
        List<HostCommandDTO> hostCommands = commandDTO.getHostCommands();

        List<String> hostnames = hostCommands.stream().map(HostCommandDTO::getHostname).toList();
        context.setHostnames(hostnames);
    }

    private static void fromServiceCommandPayload(StageContext context, CommandDTO commandDTO) {
        // No need to set anything here, we should deal with this in the service job factory
    }

    private static void fromComponentCommandPayload(StageContext context, CommandDTO commandDTO) {
        // No need to set anything here, we should deal with this in the component job factory
    }
}
