package org.apache.bigtop.manager.server.listener.factory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.HostCheckPayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.listener.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.command.HostCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;

@Slf4j
@org.springframework.stereotype.Component
public class HostJobFactory implements JobFactory, StageCallback {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private HostCacheUtils hostCacheUtils;

    @Resource
    private HostService hostService;

    @Override
    public CommandLevel getCommandLevel() {
        return CommandLevel.HOST;
    }

    public Job createJob(JobContext context) {
        Long clusterId = context.getCommandDTO().getClusterId();
        List<String> hostnames = context.getCommandDTO().getHostCommands().stream().map(HostCommandDTO::getHostname).toList();
        Job job = new Job();
        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        // Create job
        job.setName("Add Hosts");
        job.setState(JobState.PENDING);
        job.setCluster(cluster);
        job = jobRepository.save(job);

        // Create stages
        createHostCheckStage(job, cluster, hostnames, 1);

        createStage(job, cluster, 2, this.getClass().getName(), JsonUtils.writeAsString(hostnames));

        return job;
    }

    public void createHostCheckStage(Job job, Cluster cluster, List<String> hostnames, int stageOrder) {
        // Create stages
        Stage hostCheckStage = new Stage();
        hostCheckStage.setJob(job);
        hostCheckStage.setName("Check Hosts");
        hostCheckStage.setState(JobState.PENDING);
        hostCheckStage.setStageOrder(stageOrder);
        hostCheckStage.setCluster(cluster);
        hostCheckStage = stageRepository.save(hostCheckStage);

        for (String hostname : hostnames) {
            Task task = new Task();
            task.setName("Check host for " + hostname);
            task.setJob(job);
            task.setStage(hostCheckStage);
            task.setCluster(cluster);
            task.setStackName(cluster.getStack().getStackName());
            task.setStackVersion(cluster.getStack().getStackVersion());
            task.setHostname(hostname);
            task.setServiceName("cluster");
            task.setServiceUser("root");
            task.setServiceGroup("root");
            task.setComponentName("bigtop-manager-agent");
            task.setCommand(Command.CUSTOM_COMMAND);
            task.setCustomCommand("check_host");
            task.setState(JobState.PENDING);

            RequestMessage requestMessage = getMessage(hostname);
            task.setContent(JsonUtils.writeAsString(requestMessage));

            task.setMessageId(requestMessage.getMessageId());
            taskRepository.save(task);
        }
    }

    private RequestMessage getMessage(String hostname) {
        HostCheckPayload hostCheckPayload = getMessagePayload(hostname);
        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setMessageType(MessageType.HOST_CHECK);
        requestMessage.setHostname(hostname);

        requestMessage.setMessagePayload(JsonUtils.writeAsString(hostCheckPayload));
        return requestMessage;
    }

    private HostCheckPayload getMessagePayload(String hostname) {
        HostCheckPayload hostCheckMessage = new HostCheckPayload();
        hostCheckMessage.setHostCheckTypes(HostCheckType.values());
        hostCheckMessage.setHostname(hostname);
        return hostCheckMessage;
    }

    public void createStage(Job job, Cluster cluster, int stageOrder, String callbackClassName, String payload) {
        hostCacheUtils.createCache(cluster);

        List<Host> hostList = hostRepository.findAllByClusterId(cluster.getId());
        List<String> hostnames = new ArrayList<>(hostList.stream().map(Host::getHostname).toList());
        hostnames.addAll(JsonUtils.readFromString(payload));

        Stage hostCacheStage = new Stage();
        hostCacheStage.setJob(job);
        hostCacheStage.setName(CACHE_STAGE_NAME);
        hostCacheStage.setState(JobState.PENDING);
        hostCacheStage.setStageOrder(stageOrder);
        hostCacheStage.setCluster(cluster);

        if (StringUtils.isNotEmpty(callbackClassName)) {
            hostCacheStage.setCallbackClassName(callbackClassName);
        } else {
            hostCacheStage.setCallbackClassName(this.getClass().getName());
        }
        if (StringUtils.isNotEmpty(callbackClassName)) {
            hostCacheStage.setPayload(payload);
        }
        hostCacheStage = stageRepository.save(hostCacheStage);

        for (String hostname : hostnames) {
            Task task = new Task();
            task.setName("Cache host for " + hostname);
            task.setJob(job);
            task.setStage(hostCacheStage);
            task.setCluster(cluster);
            task.setStackName(cluster.getStack().getStackName());
            task.setStackVersion(cluster.getStack().getStackVersion());
            task.setHostname(hostname);
            task.setServiceName("cluster");
            task.setServiceUser("root");
            task.setServiceGroup("root");
            task.setComponentName("bigtop-manager-agent");
            task.setCommand(Command.CUSTOM_COMMAND);
            task.setCustomCommand("cache_host");
            task.setState(JobState.PENDING);

            RequestMessage requestMessage = getMessage(hostname);
            log.info("[HostCacheJobFactory-requestMessage]: {}", requestMessage);
            task.setContent(JsonUtils.writeAsString(requestMessage));

            task.setMessageId(requestMessage.getMessageId());
            taskRepository.save(task);
        }
    }

    @Override
    public void beforeStage(Stage stage) {
        Cluster cluster = stage.getCluster();
        if (stage.getName().equals(CACHE_STAGE_NAME)) {
            List<String> hostnames = JsonUtils.readFromString(stage.getPayload());
            hostService.batchSave(cluster.getId(), hostnames);
        }
    }

    @Override
    public String generatePayload(Task task) {
        Cluster cluster = task.getCluster();
        hostCacheUtils.createCache(cluster);
        RequestMessage requestMessage = hostCacheUtils.getMessage(task.getHostname());
        log.info("[generatePayload]-[HostCacheJobFactory-requestMessage]: {}", requestMessage);
        return JsonUtils.writeAsString(requestMessage);
    }
}
