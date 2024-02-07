package org.apache.bigtop.manager.server.job.factory.component;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.common.enums.JobState;
import org.apache.bigtop.manager.common.enums.MaintainState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.ComponentCommandDTO;
import org.apache.bigtop.manager.dao.entity.*;
import org.apache.bigtop.manager.dao.repository.HostComponentRepository;
import org.apache.bigtop.manager.dao.repository.ServiceRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComponentStopJobFactory extends AbstractComponentJobFactory implements StageCallback {

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.COMPONENT, Command.STOP);
    }

    @Override
    public void afterStage(Stage stage) {
        Long clusterId = stage.getCluster().getId();
        String componentName = stage.getComponentName();
        CommandDTO commandDTO = JsonUtils.readFromString(stage.getPayload(), CommandDTO.class);
        List<ComponentCommandDTO> componentCommands = commandDTO.getComponentCommands();
        List<String> hostnames = componentCommands
                .stream()
                .filter(x -> x.getComponentName().equals(componentName))
                .flatMap(x -> x.getHostnames().stream())
                .toList();

        if (stage.getState() == JobState.SUCCESSFUL) {
            List<HostComponent> hostComponents = hostComponentRepository
                    .findAllByComponentClusterIdAndComponentComponentNameAndHostHostnameIn(clusterId, componentName, hostnames);
            Service service = hostComponents.get(0).getComponent().getService();

            // Update the state of the host component
            hostComponents.forEach(hostComponent -> hostComponent.setState(MaintainState.STOPPED));
            hostComponentRepository.saveAll(hostComponents);

            if (hostComponents.stream().allMatch(x -> x.getState() == MaintainState.STOPPED)) {
                service.setState(MaintainState.STOPPED);
            }
            serviceRepository.save(service);
        }
    }

}
