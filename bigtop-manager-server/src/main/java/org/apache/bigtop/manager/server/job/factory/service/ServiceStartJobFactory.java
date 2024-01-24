package org.apache.bigtop.manager.server.job.factory.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.entity.Service;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceStartJobFactory extends AbstractServiceJobFactory implements StageCallback {

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.SERVICE, Command.START);
    }

    @Override
    public void afterStage(Stage stage) {
        Long clusterId = stage.getCluster().getId();
        String componentName = stage.getComponentName();

        if (stage.getState() == JobState.SUCCESSFUL) {
            List<HostComponent> hostComponents = hostComponentRepository.findAllByComponentClusterIdAndComponentComponentName(clusterId, componentName);
            Service service = hostComponents.get(0).getComponent().getService();

            hostComponents.forEach(hostComponent -> hostComponent.setState(MaintainState.STARTED));
            hostComponentRepository.saveAll(hostComponents);

            if (hostComponents.stream().allMatch(x -> x.getState() == MaintainState.STARTED)) {
                service.setState(MaintainState.STARTED);
            }
            serviceRepository.save(service);
        }
    }

}
