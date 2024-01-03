package org.apache.bigtop.manager.server.listener.factory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.orm.repository.StageRepository;
import org.apache.bigtop.manager.server.orm.repository.TaskRepository;
import org.apache.bigtop.manager.server.utils.LogUtils;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

@Slf4j
@org.springframework.stereotype.Component
public class ClusterCreateJobFactory implements JobFactory {

    @Resource
    private StackRepository stackRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private HostAddJobFactory hostAddJobFactory;

    @Resource
    private HostCacheJobFactory hostCacheJobFactory;

    public Job createJob(ClusterDTO clusterDTO) {
        Stack stack = stackRepository.findByStackNameAndStackVersion(clusterDTO.getStackName(), clusterDTO.getStackVersion());
        Cluster cluster = new Cluster();
        cluster.setStack(stack);
        // Create job
        Job job = new Job();
        job.setTraceId(LogUtils.getTraceId());
        job.setContext("Create Cluster");
        job.setState(JobState.PENDING);
        job.setCluster(cluster);
        job = jobRepository.save(job);

        hostAddJobFactory.createHostCheckStage(job, cluster, clusterDTO.getHostnames(), 1);

        createCacheStage(job, clusterDTO, 2);

        return job;
    }

    public void createCacheStage(Job job, ClusterDTO clusterDTO, int stageOrder) {
        Map<String, ComponentInfo> componentInfoMap = new HashMap<>();
        Map<String, Map<String, Object>> serviceConfigMap = new HashMap<>();
        Map<String, Set<String>> hostMap = new HashMap<>();
        Map<String, Set<String>> userMap = new HashMap<>();
        Map<String, Object> settingsMap = new HashMap<>();

        ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = StackUtils.getStackKeyMap().get(StackUtils.fullStackName(clusterDTO.getStackName(), clusterDTO.getStackVersion()));
        StackDTO stackDTO = immutablePair.getLeft();
        List<ServiceDTO> serviceDTOList = immutablePair.getRight();

        List<RepoInfo> repoList = RepoMapper.INSTANCE.fromDTO2Message(clusterDTO.getRepoInfoList());
        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterName(clusterDTO.getClusterName());
        clusterInfo.setStackName(clusterDTO.getStackName());
        clusterInfo.setStackVersion(clusterDTO.getStackVersion());
        clusterInfo.setUserGroup(stackDTO.getUserGroup());
        clusterInfo.setRepoTemplate(stackDTO.getRepoTemplate());
        clusterInfo.setRoot(stackDTO.getRoot());

        List<String> hostnames = clusterDTO.getHostnames();
        hostMap.put(Constants.ALL_HOST_KEY, new HashSet<>(hostnames));

        for (ServiceDTO serviceDTO : serviceDTOList) {
            userMap.put(serviceDTO.getServiceUser(), Set.of(serviceDTO.getServiceGroup()));
        }

        Stage hostCacheStage = new Stage();
        hostCacheStage.setTraceId(job.getTraceId());
        hostCacheStage.setJob(job);
        hostCacheStage.setName("Cache Host");
        hostCacheStage.setState(JobState.PENDING);
        hostCacheStage.setStageOrder(stageOrder);
        hostCacheStage = stageRepository.save(hostCacheStage);

        for (String hostname : hostnames) {
            Task task = new Task();
            task.setJob(job);
            task.setStage(hostCacheStage);
            task.setStackName(clusterDTO.getStackName());
            task.setStackVersion(clusterDTO.getStackVersion());
            task.setHostname(hostname);
            task.setServiceName("cluster");
            task.setServiceUser("root");
            task.setServiceGroup("root");
            task.setComponentName("bigtop-manager-agent");
            task.setCommand(Command.CUSTOM_COMMAND);
            task.setCustomCommand("cache_host");
            task.setState(JobState.PENDING);
            task.setTraceId(job.getTraceId());

            RequestMessage requestMessage = hostCacheJobFactory.getMessage(
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
