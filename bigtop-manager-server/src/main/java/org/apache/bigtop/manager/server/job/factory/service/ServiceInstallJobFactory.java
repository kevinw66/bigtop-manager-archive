package org.apache.bigtop.manager.server.job.factory.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.HostCacheStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.*;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;
import org.apache.bigtop.manager.server.service.ServiceService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceInstallJobFactory extends AbstractServiceJobFactory implements StageCallback {

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ServiceService serviceService;

    @Resource
    private HostCacheStageHelper hostCacheStageHelper;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.SERVICE, Command.INSTALL);
    }

    /**
     * create job and persist it to database
     */
    @Override
    protected List<Stage> createStagesAndTasks() {
        String callbackClassName = this.getClass().getName();

        // Install all required components in service
        List<Stage> stages = new ArrayList<>(createStages(callbackClassName));

        // cache stage
        String payload = JsonUtils.writeAsString(jobContext.getCommandDTO());
        stages.add(hostCacheStageHelper.createStage(cluster.getId(), callbackClassName, payload));

        // Start all required components in service
        stages.addAll(createStages(callbackClassName, Command.START));

        // Check all required components in service
        stages.addAll(createStages(callbackClassName, Command.CHECK));

        return stages;
    }

    @Override
    protected Map<String, List<String>> getComponentHostMapping(Command command) {
        return jobContext.getCommandDTO().getServiceCommands().stream()
                .flatMap(serviceCommand -> serviceCommand.getComponentHosts().stream())
                .collect(Collectors.toMap(ComponentHostDTO::getComponentName, componentHostDTO -> {
                    if (command == Command.CHECK) {
                        return List.of(componentHostDTO.getHostnames().get(0));
                    } else {
                        return componentHostDTO.getHostnames();
                    }
                }));
    }

    @Override
    protected List<Component> getComponents() {
        List<Component> components = new ArrayList<>();
        CommandDTO commandDTO = jobContext.getCommandDTO();
        Long clusterId = commandDTO.getClusterId();

        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();

        Map<String, ImmutablePair<StackDTO, List<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

        ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));
        Map<String, ServiceDTO> serviceNameToDTO = immutablePair.getRight()
                .stream()
                .collect(Collectors.toMap(ServiceDTO::getServiceName, Function.identity()));

        // Persist service, component and hostComponent metadata to database
        for (ServiceCommandDTO serviceCommand : commandDTO.getServiceCommands()) {
            String serviceName = serviceCommand.getServiceName();
            Optional<Service> serviceOptional = serviceRepository.findByClusterIdAndServiceName(clusterId, serviceName);
            if (serviceOptional.isPresent()) {
                continue;
            }

            ServiceDTO serviceDTO = serviceNameToDTO.get(serviceName);
            Service service = ServiceMapper.INSTANCE.fromDTO2Entity(serviceDTO, cluster);
            List<ComponentDTO> componentDTOList = serviceDTO.getComponents();
            for (ComponentDTO componentDTO : componentDTOList) {
                Component component = ComponentMapper.INSTANCE.fromDTO2Entity(componentDTO, service, cluster);
                components.add(component);
            }
        }

        return components;
    }

    @Override
    public void beforeStage(Stage stage) {
        CommandDTO commandDTO = JsonUtils.readFromString(stage.getPayload(), CommandDTO.class);
        if (stage.getName().equals(CACHE_STAGE_NAME) && commandDTO.getCommand() == Command.INSTALL && commandDTO.getCommandLevel() == CommandLevel.SERVICE) {
            serviceService.saveByCommand(commandDTO);
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

    @Override
    public void afterStage(Stage stage) {
        Long clusterId = stage.getCluster().getId();
        String componentName = stage.getComponentName();
        CommandDTO commandDTO = JsonUtils.readFromString(stage.getPayload(), CommandDTO.class);
        Command command = commandDTO.getCommand();

        if (stage.getState() == JobState.SUCCESSFUL && command == Command.START) {
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
