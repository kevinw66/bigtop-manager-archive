package org.apache.bigtop.manager.server.job.factory.host;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.HostCacheStageHelper;
import org.apache.bigtop.manager.server.job.helper.HostCheckStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.command.HostCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HostAddJobFactory extends AbstractHostJobFactory implements StageCallback {

    @Resource
    private HostRepository hostRepository;

    @Resource
    private HostCacheStageHelper hostCacheStageHelper;

    @Resource
    private HostService hostService;

    @Resource
    private HostCheckStageHelper hostCheckStageHelper;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.HOST, Command.INSTALL);
    }

    @Override
    public List<Stage> createStagesAndTasks() {
        List<Stage> stages = new ArrayList<>();
        String callbackClassName = this.getClass().getName();
        Long clusterId = jobContext.getCommandDTO().getClusterId();
        List<String> hostnames = jobContext.getCommandDTO().getHostCommands().stream().map(HostCommandDTO::getHostname).toList();

        // Create stages
        stages.add(hostCheckStageHelper.createStage(clusterId, hostnames, callbackClassName));
        stages.add(createStage(callbackClassName, JsonUtils.writeAsString(hostnames)));

        return stages;
    }

    public Stage createStage(String callbackClassName, String payload) {
        hostCacheStageHelper.createCache(cluster);

        List<Host> hostList = hostRepository.findAllByClusterId(cluster.getId());
        List<String> hostnames = new ArrayList<>(hostList.stream().map(Host::getHostname).toList());
        hostnames.addAll(JsonUtils.readFromString(payload));

        Stage hostCacheStage = new Stage();
        hostCacheStage.setName(CACHE_STAGE_NAME);
        hostCacheStage.setState(JobState.PENDING);

        if (StringUtils.isNotEmpty(callbackClassName)) {
            hostCacheStage.setCallbackClassName(callbackClassName);
        } else {
            hostCacheStage.setCallbackClassName(this.getClass().getName());
        }

        if (StringUtils.isNotEmpty(callbackClassName)) {
            hostCacheStage.setPayload(payload);
        }

        List<Task> tasks = new ArrayList<>();
        for (String hostname : hostnames) {
            Task task = new Task();
            task.setName("Cache host for " + hostname);
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

            RequestMessage requestMessage = hostCheckStageHelper.createMessage(hostname);
            log.info("[HostCacheJobFactory-requestMessage]: {}", requestMessage);
            task.setContent(JsonUtils.writeAsString(requestMessage));
            task.setMessageId(requestMessage.getMessageId());

            tasks.add(task);
        }

        hostCacheStage.setTasks(tasks);
        return hostCacheStage;
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
        hostCacheStageHelper.createCache(cluster);
        RequestMessage requestMessage = hostCacheStageHelper.getMessage(task.getHostname());
        log.info("[generatePayload]-[HostCacheJobFactory-requestMessage]: {}", requestMessage);
        return JsonUtils.writeAsString(requestMessage);
    }
}
