package org.apache.bigtop.manager.server.command.job.factory.service;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.command.job.factory.AbstractJobFactory;
import org.apache.bigtop.manager.server.command.stage.factory.StageContext;
import org.apache.bigtop.manager.server.command.stage.factory.StageFactories;
import org.apache.bigtop.manager.server.command.stage.factory.StageType;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.dao.entity.Component;
import org.apache.bigtop.manager.dao.entity.Host;
import org.apache.bigtop.manager.dao.entity.HostComponent;
import org.apache.bigtop.manager.dao.repository.ComponentRepository;
import org.apache.bigtop.manager.dao.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.stack.dag.ComponentCommandWrapper;
import org.apache.bigtop.manager.server.stack.dag.DAG;
import org.apache.bigtop.manager.server.stack.dag.DagGraphEdge;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A Service Job can be seen as a collection of multiple Components and Hosts,
 * so it can directly inherit from AbstractComponentJobFactory.
 */
public abstract class AbstractServiceJobFactory extends AbstractJobFactory {

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    protected String stackName;

    protected String stackVersion;

    protected Map<String, ServiceDTO> serviceNameToDTO;

    protected Map<String, ComponentDTO> componentNameToDTO;

    protected DAG<String, ComponentCommandWrapper, DagGraphEdge> dag;

    protected void initAttrs() {
        stackName = cluster.getStack().getStackName();
        stackVersion = cluster.getStack().getStackVersion();

        Map<String, ImmutablePair<StackDTO, List<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();
        ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));

        serviceNameToDTO = immutablePair.getRight()
                .stream()
                .collect(Collectors.toMap(ServiceDTO::getServiceName, Function.identity()));

        componentNameToDTO = immutablePair.getRight()
                .stream()
                .flatMap(serviceDTO -> serviceDTO.getComponents().stream())
                .collect(Collectors.toMap(ComponentDTO::getComponentName, Function.identity()));

        dag = StackUtils.getStackDagMap().get(StackUtils.fullStackName(stackName, stackVersion));
    }

    protected StageContext createStageContext(String serviceName, String componentName) {
        return createStageContext(serviceName, componentName, findHostnamesByComponentName(componentName));
    }

    protected StageContext createStageContext(String serviceName, String componentName, List<String> hostnames) {
        ServiceDTO serviceDTO = serviceNameToDTO.get(serviceName);
        ComponentDTO componentDTO = componentNameToDTO.get(componentName);

        StageContext stageContext = StageContext.fromPayload(JsonUtils.writeAsString(jobContext.getCommandDTO()));
        stageContext.setServiceDTO(serviceDTO);
        stageContext.setComponentDTO(componentDTO);
        stageContext.setStackName(stackName);
        stageContext.setStackVersion(stackVersion);
        stageContext.setHostnames(hostnames);

        return stageContext;
    }

    protected List<String> getTodoListForCommand(Command command) {
        try {
            List<String> orderedList = dag.topologicalSort();
            List<String> componentNames = getComponentNames();
            List<String> componentCommandNames = new ArrayList<>(componentNames.stream().map(x -> x + "-" + command.name().toUpperCase()).toList());

            orderedList.retainAll(componentCommandNames);
            componentCommandNames.removeAll(orderedList);
            orderedList.addAll(componentCommandNames);

            return orderedList;
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

    protected List<String> getComponentNames() {
        List<String> serviceNames = getServiceNames();
        List<Component> components = componentRepository.findAllByClusterIdAndServiceServiceNameIn(cluster.getId(), serviceNames);

        return components.stream().map(Component::getComponentName).toList();
    }

    protected String findServiceNameByComponentName(String componentName) {
        for (ServiceDTO serviceDTO : serviceNameToDTO.values()) {
            for (ComponentDTO componentDTO : serviceDTO.getComponents()) {
                if (componentDTO.getComponentName().equals(componentName)) {
                    return serviceDTO.getServiceName();
                }
            }
        }

        return null;
    }

    protected Boolean isMasterComponent(String componentName) {
        ComponentDTO componentDTO = componentNameToDTO.get(componentName);
        return componentDTO.getCategory().equalsIgnoreCase("master");
    }

    protected List<String> findHostnamesByComponentName(String componentName) {
        List<HostComponent> hostComponents = hostComponentRepository.findAllByComponentClusterIdAndComponentComponentName(cluster.getId(), componentName);
        if (hostComponents == null) {
            return new ArrayList<>();
        } else {
            return hostComponents.stream().map(HostComponent::getHost).map(Host::getHostname).toList();
        }
    }

    protected void createCacheStage() {
        StageContext stageContext = StageContext.fromPayload(JsonUtils.writeAsString(jobContext.getCommandDTO()));
        stageContext.setStackName(stackName);
        stageContext.setStackVersion(stackVersion);
        stages.add(StageFactories.getStageFactory(StageType.CACHE_DISTRIBUTE).createStage(stageContext));
    }

    protected void createInstallStages() {
        List<String> todoList = getTodoListForCommand(Command.INSTALL);

        for (String componentCommand : todoList) {
            String[] split = componentCommand.split("-");
            String componentName = split[0];
            String serviceName = findServiceNameByComponentName(componentName);

            StageContext stageContext = createStageContext(serviceName, componentName);
            stages.add(StageFactories.getStageFactory(StageType.COMPONENT_INSTALL).createStage(stageContext));
        }
    }

    protected void createStartStages() {
        List<String> todoList = getTodoListForCommand(Command.START);

        for (String componentCommand : todoList) {
            String[] split = componentCommand.split("-");
            String componentName = split[0];
            String serviceName = findServiceNameByComponentName(componentName);

            if (!isMasterComponent(componentName)) {
                continue;
            }

            StageContext stageContext = createStageContext(serviceName, componentName);
            stages.add(StageFactories.getStageFactory(StageType.COMPONENT_START).createStage(stageContext));
        }
    }

    protected void createStopStages() {
        List<String> todoList = getTodoListForCommand(Command.STOP);

        for (String componentCommand : todoList) {
            String[] split = componentCommand.split("-");
            String componentName = split[0];
            String serviceName = findServiceNameByComponentName(componentName);

            if (!isMasterComponent(componentName)) {
                continue;
            }

            StageContext stageContext = createStageContext(serviceName, componentName);
            stages.add(StageFactories.getStageFactory(StageType.COMPONENT_STOP).createStage(stageContext));
        }
    }

    protected void createCheckStages() {
        List<String> todoList = getTodoListForCommand(Command.CHECK);

        for (String componentCommand : todoList) {
            String[] split = componentCommand.split("-");
            String componentName = split[0];
            String serviceName = findServiceNameByComponentName(componentName);
            List<String> hostnames = findHostnamesByComponentName(componentName);

            if (!isMasterComponent(componentName)) {
                continue;
            }

            StageContext stageContext = createStageContext(serviceName, componentName, List.of(hostnames.get(0)));
            stages.add(StageFactories.getStageFactory(StageType.COMPONENT_CHECK).createStage(stageContext));
        }
    }

    private List<String> getServiceNames() {
        return jobContext.getCommandDTO().getServiceCommands().stream().map(ServiceCommandDTO::getServiceName).toList();
    }
}
