package org.apache.bigtop.manager.server.job.factory;

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
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.HostCacheStageHelper;
import org.apache.bigtop.manager.server.job.helper.HostCheckStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.dto.command.ClusterCommandDTO;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.orm.repository.StageRepository;
import org.apache.bigtop.manager.server.orm.repository.TaskRepository;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.BeanUtils;

import java.util.*;

import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;

@Slf4j
@org.springframework.stereotype.Component
public class ClusterAddJobFactory implements JobFactory, StageCallback {

    @Resource
    private StackRepository stackRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private HostCheckStageHelper hostCheckStageHelper;

    @Resource
    private HostCacheStageHelper hostCacheStageHelper;

    @Resource
    private ClusterService clusterService;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.CLUSTER, Command.INSTALL);
    }

    public Job createJob(JobContext context) {
        ClusterCommandDTO clusterCommand = context.getCommandDTO().getClusterCommand();
        Stack stack = stackRepository.findByStackNameAndStackVersion(clusterCommand.getStackName(), clusterCommand.getStackVersion());
        Cluster cluster = new Cluster();
        cluster.setStack(stack);
        // Create job
        Job job = new Job();
        job.setName("Create Cluster");
        job.setState(JobState.PENDING);
        job.setCluster(cluster);
        job = jobRepository.save(job);

        hostCheckStageHelper.createStage(job, cluster, clusterCommand.getHostnames(), 1);

        createCacheStage(job, clusterCommand, 2, this.getClass().getName(), JsonUtils.writeAsString(clusterCommand));

        return job;
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

    public void createCacheStage(Job job, ClusterCommandDTO clusterCommand, int stageOrder, String callbackClassName, String payload) {
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
        hostCacheStage.setJob(job);
        hostCacheStage.setName(CACHE_STAGE_NAME);
        hostCacheStage.setState(JobState.PENDING);
        hostCacheStage.setStageOrder(stageOrder);
        hostCacheStage.setCallbackClassName(callbackClassName);
        hostCacheStage.setPayload(payload);
        hostCacheStage = stageRepository.save(hostCacheStage);

        for (String hostname : hostnames) {
            Task task = new Task();
            task.setName("Cache host for " + hostname);
            task.setJob(job);
            task.setStage(hostCacheStage);
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
            taskRepository.save(task);
        }
    }
}
