package org.apache.bigtop.manager.server.job.factory.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.HostCacheStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceConfigDTO;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.dao.entity.Cluster;
import org.apache.bigtop.manager.dao.entity.Stage;
import org.apache.bigtop.manager.dao.repository.ClusterRepository;
import org.apache.bigtop.manager.server.service.ConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceConfigureJobFactory extends AbstractServiceJobFactory implements StageCallback {

    @Resource
    private HostCacheStageHelper hostCacheStageHelper;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private ConfigService configService;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.SERVICE, Command.CONFIGURE);
    }

    @Override
    public List<Stage> createStagesAndTasks() {
        String callbackClassName = this.getClass().getName();
        String payload = JsonUtils.writeAsString(jobContext.getCommandDTO());
        return List.of(hostCacheStageHelper.createStage(cluster.getId(), callbackClassName, payload));
    }

    @Override
    public void beforeStage(Stage stage) {
        CommandDTO commandDTO = JsonUtils.readFromString(stage.getPayload(), CommandDTO.class);

        Cluster cluster = clusterRepository.getReferenceById(commandDTO.getClusterId());
        for (ServiceCommandDTO serviceCommand : commandDTO.getServiceCommands()) {
            ServiceConfigDTO serviceConfigDTO = new ServiceConfigDTO();
            BeanUtils.copyProperties(serviceCommand, serviceConfigDTO);
            configService.updateConfig(cluster, serviceConfigDTO);
        }
    }
}
