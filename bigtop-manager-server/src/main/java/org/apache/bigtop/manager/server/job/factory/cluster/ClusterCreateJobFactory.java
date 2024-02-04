package org.apache.bigtop.manager.server.job.factory.cluster;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.common.enums.JobState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.HostCacheStageHelper;
import org.apache.bigtop.manager.server.job.helper.HostCheckStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.dto.command.ClusterCommandDTO;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.dao.entity.Stage;
import org.apache.bigtop.manager.dao.entity.Task;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;

import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClusterCreateJobFactory extends AbstractClusterJobFactory implements StageCallback {

    @Resource
    private HostCheckStageHelper hostCheckStageHelper;

    @Resource
    private HostCacheStageHelper hostCacheStageHelper;

    @Resource
    private ClusterService clusterService;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.CLUSTER, Command.CREATE);
    }

    @Override
    public List<Stage> createStagesAndTasks() {
        List<Stage> stages = new ArrayList<>();
        String callbackClassName = this.getClass().getName();
        ClusterCommandDTO clusterCommand = jobContext.getCommandDTO().getClusterCommand();
        List<String> hostnames = clusterCommand.getHostnames();

        stages.add(hostCheckStageHelper.createStage(clusterCommand.getStackName(), clusterCommand.getStackVersion(), hostnames, callbackClassName));
        stages.add(createCacheStage(clusterCommand, callbackClassName, JsonUtils.writeAsString(clusterCommand)));

        return stages;
    }

    @Override
    public void beforeStage(Stage stage) {
        if (stage.getName().equals(CACHE_STAGE_NAME)) {
            ClusterCommandDTO clusterCommand = JsonUtils.readFromString(stage.getPayload(), ClusterCommandDTO.class);
            ClusterDTO clusterDTO = new ClusterDTO();
            BeanUtils.copyProperties(clusterCommand, clusterDTO);

            clusterService.save(clusterDTO);
        }
    }

    public Stage createCacheStage(ClusterCommandDTO clusterCommand, String callbackClassName, String payload) {
        Map<String, ComponentInfo> componentInfoMap = new HashMap<>();
        Map<String, Map<String, Object>> serviceConfigMap = new HashMap<>();
        Map<String, Set<String>> hostMap = new HashMap<>();
        Map<String, Set<String>> userMap = new HashMap<>();
        Map<String, Object> settingsMap = new HashMap<>();

        String fullStackName = StackUtils.fullStackName(clusterCommand.getStackName(), clusterCommand.getStackVersion());
        ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = StackUtils.getStackKeyMap().get(fullStackName);
        StackDTO stackDTO = immutablePair.getLeft();
        List<ServiceDTO> serviceDTOList = immutablePair.getRight();

        List<RepoInfo> repoList = RepoMapper.INSTANCE.fromDTO2Message(clusterCommand.getRepoInfoList());
        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterName(clusterCommand.getClusterName());
        clusterInfo.setStackName(clusterCommand.getStackName());
        clusterInfo.setStackVersion(clusterCommand.getStackVersion());
        clusterInfo.setUserGroup(stackDTO.getUserGroup());
        clusterInfo.setRepoTemplate(stackDTO.getRepoTemplate());
        clusterInfo.setRoot(stackDTO.getRoot());

        List<String> hostnames = clusterCommand.getHostnames();
        hostMap.put(Constants.ALL_HOST_KEY, new HashSet<>(hostnames));

        for (ServiceDTO serviceDTO : serviceDTOList) {
            userMap.put(serviceDTO.getServiceUser(), Set.of(serviceDTO.getServiceGroup()));
        }

        Stage hostCacheStage = new Stage();
        hostCacheStage.setName(CACHE_STAGE_NAME);
        hostCacheStage.setState(JobState.PENDING);
        hostCacheStage.setCallbackClassName(callbackClassName);
        hostCacheStage.setPayload(payload);

        List<Task> tasks = new ArrayList<>();
        for (String hostname : hostnames) {
            Task task = new Task();
            task.setName("Cache host for " + hostname);
            task.setStackName(clusterCommand.getStackName());
            task.setStackVersion(clusterCommand.getStackVersion());
            task.setHostname(hostname);
            task.setServiceName("cluster");
            task.setServiceUser("root");
            task.setServiceGroup("root");
            task.setComponentName("bigtop-manager-agent");
            task.setCommand(Command.CUSTOM_COMMAND);
            task.setCustomCommand("cache_host");
            task.setState(JobState.PENDING);

            RequestMessage requestMessage = hostCacheStageHelper.getMessage(
                    hostname,
                    settingsMap,
                    clusterInfo,
                    serviceConfigMap,
                    hostMap,
                    repoList,
                    userMap,
                    componentInfoMap);

            log.info("[HostCacheJobFactory-requestMessage]: {}", requestMessage);
            task.setContent(JsonUtils.writeAsString(requestMessage));
            task.setMessageId(requestMessage.getMessageId());
            tasks.add(task);
        }

        hostCacheStage.setTasks(tasks);
        return hostCacheStage;
    }
}
