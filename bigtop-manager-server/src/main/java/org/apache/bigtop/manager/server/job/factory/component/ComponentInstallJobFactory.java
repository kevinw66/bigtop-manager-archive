package org.apache.bigtop.manager.server.job.factory.component;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.HostCacheStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.dao.entity.Cluster;
import org.apache.bigtop.manager.dao.entity.Stage;
import org.apache.bigtop.manager.dao.entity.Task;
import org.apache.bigtop.manager.server.service.HostComponentService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComponentInstallJobFactory extends AbstractComponentJobFactory implements StageCallback {

    @Resource
    private HostCacheStageHelper hostCacheStageHelper;

    @Resource
    private HostComponentService hostComponentService;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.COMPONENT, Command.INSTALL);
    }

    @Override
    public List<Stage> createStagesAndTasks() {
        List<Stage> stages = super.createStagesAndTasks();

        String callbackClassName = this.getClass().getName();
        String payload = JsonUtils.writeAsString(jobContext.getCommandDTO());
        stages.add(hostCacheStageHelper.createStage(cluster.getId(), callbackClassName, payload));

        return stages;
    }

    @Override
    public void beforeStage(Stage stage) {
        CommandDTO commandDTO = JsonUtils.readFromString(stage.getPayload(), CommandDTO.class);
        if (stage.getName().equals(CACHE_STAGE_NAME) && commandDTO.getCommand() == Command.INSTALL && commandDTO.getCommandLevel() == CommandLevel.COMPONENT) {
            hostComponentService.saveByCommand(commandDTO);
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
