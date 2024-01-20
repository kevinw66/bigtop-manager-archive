package org.apache.bigtop.manager.server.job.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.pojo.CustomCommandInfo;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ScriptInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.*;
import org.apache.bigtop.manager.server.model.dto.command.ComponentCommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.stack.dag.ComponentCommandWrapper;
import org.apache.bigtop.manager.server.stack.dag.DAG;
import org.apache.bigtop.manager.server.stack.dag.DagGraphEdge;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.text.CaseUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Component
public class ServiceComponentStageHelper {

    private final Random random = new Random();

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    public int createStage(Job job, CommandDTO commandDTO, int stageOrder, String callbackClassName) {
        Command command = commandDTO.getCommand();
        String customCommand = commandDTO.getCustomCommand();
        Long clusterId = commandDTO.getClusterId();
        Cluster cluster = clusterRepository.getReferenceById(clusterId);
        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();

        List<ComponentCommandWrapper> componentCommandWrappers = createCommandWrapper(commandDTO);
        List<ComponentCommandWrapper> sortedRcpList = stageSort(componentCommandWrappers, stackName, stackVersion);
        Map<String, List<String>> componentHostMapping = createComponentHostMapping(sortedRcpList, commandDTO);

        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < sortedRcpList.size(); i++) {
            ComponentCommandWrapper componentCommandWrapper = sortedRcpList.get(i);
            String componentName = componentCommandWrapper.getComponentName();
            Component component = componentCommandWrapper.getComponent();
            if (component == null) {
                throw new ApiException(ApiExceptionEnum.COMPONENT_NOT_FOUND);
            }
            Stage stage = new Stage();
            stage.setJob(job);
            stage.setCluster(job.getCluster());
            stage.setState(JobState.PENDING);
            stage.setName(componentCommandWrapper.toDisplayString());
            stage.setStageOrder(stageOrder + i + 1);
            stage.setServiceName(component.getService().getServiceName());
            stage.setComponentName(componentName);
            log.debug("stage: {}", stage);
            // Set stage callback
            stage.setCallbackClassName(callbackClassName);
            stage.setPayload(JsonUtils.writeAsString(commandDTO));
            stage = stageRepository.save(stage);

            List<String> hostnames = componentHostMapping.get(componentName);
            // Generate task list
            for (String hostname : hostnames) {
                Task task = createTask(component, hostname, command, job, stage, customCommand);
                log.debug("task: {}", task);
                tasks.add(task);
            }

        }
        taskRepository.saveAll(tasks);

        return stageOrder + sortedRcpList.size();
    }

    /**
     * Execute tasks based on DAG serial sorting
     * Can be optimized for parallel execution
     *
     * @param todoList     request list
     * @param stackName    stack name
     * @param stackVersion stack version
     */
    private List<ComponentCommandWrapper> stageSort(List<ComponentCommandWrapper> todoList, String
            stackName, String stackVersion) {
        List<ComponentCommandWrapper> sortedList = new ArrayList<>();

        DAG<String, ComponentCommandWrapper, DagGraphEdge> dag = StackUtils.getStackDagMap().get(StackUtils.fullStackName(stackName, stackVersion));
        log.info("todolist: {}", todoList);
        try {
            List<String> orderedList = dag.topologicalSort();
            log.info("DAG topological sort list: {}", orderedList);

            for (String node : orderedList) {
                ComponentCommandWrapper nodeInfo = dag.getNode(node);
                for (ComponentCommandWrapper componentCommandWrapper : todoList) {
                    if (componentCommandWrapper.equals(nodeInfo)) {
                        sortedList.add(componentCommandWrapper);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServerException(e);
        }

        todoList.removeAll(sortedList);
        sortedList.addAll(todoList);

        log.info("Sorted list: {}", sortedList);
        return sortedList;
    }

    /**
     * Fill in the mapping of components and hosts
     * componentHostMapping key: component name, value: host list
     *
     * @param sortedRcpList result of {@link #stageSort}
     * @param commandDTO    command event
     * @return componentHostMapping key: component name, value: host list. e.g. {zookeeper_server=[node1], kafka_broker=[node1, node2]}
     */
    private Map<String, List<String>> createComponentHostMapping
    (List<ComponentCommandWrapper> sortedRcpList, CommandDTO commandDTO) throws ApiException {
        Map<String, List<String>> componentHostMapping = new HashMap<>();

        Long clusterId = commandDTO.getClusterId();

        switch (commandDTO.getCommandLevel()) {
            case INTERNAL_SERVICE_INSTALL -> {
                componentHostMapping = commandDTO.getServiceCommands()
                        .stream()
                        .flatMap(x -> x.getComponentHosts().stream())
                        .collect(Collectors.toMap(ComponentHostDTO::getComponentName, ComponentHostDTO::getHostnames));

                Command command = commandDTO.getCommand();
                if (command == Command.CHECK) {
                    HashMap<String, List<String>> map = new HashMap<>();
                    for (Map.Entry<String, List<String>> entry : componentHostMapping.entrySet()) {
                        String componentName = entry.getKey();
                        int index = random.nextInt(entry.getValue().size());
                        map.put(componentName, List.of(entry.getValue().get(index)));
                    }
                    componentHostMapping = map;
                }
            }
            case SERVICE -> {
                if (commandDTO.getCommand() == Command.INSTALL && commandDTO.getCommandLevel() == CommandLevel.SERVICE) {
                    componentHostMapping = commandDTO.getServiceCommands()
                            .stream()
                            .flatMap(x -> x.getComponentHosts().stream())
                            .collect(Collectors.toMap(ComponentHostDTO::getComponentName, ComponentHostDTO::getHostnames));
                } else {
                    for (ComponentCommandWrapper componentCommandWrapper : sortedRcpList) {
                        String componentName = componentCommandWrapper.getComponentName();
                        List<HostComponent> hostComponentList = hostComponentRepository.findAllByComponentClusterIdAndComponentComponentName(clusterId, componentName);

                        List<String> hostnames = hostComponentList.stream().map(x -> x.getHost().getHostname()).toList();

                        Command command = componentCommandWrapper.getCommand();
                        if (command == Command.CHECK) {
                            int index = random.nextInt(hostComponentList.size());
                            hostnames = List.of(hostComponentList.get(index).getHost().getHostname());
                        }

                        componentHostMapping.put(componentName, hostnames);
                    }
                }
            }
            case COMPONENT -> {
                List<ComponentCommandDTO> componentCommands = commandDTO.getComponentCommands();
                componentHostMapping = componentCommands.stream().collect(Collectors.toMap(ComponentCommandDTO::getComponentName, ComponentCommandDTO::getHostnames));
            }
            default -> log.warn("Unknown commandType: {}", commandDTO);
        }

        validateHost(componentHostMapping);

        return componentHostMapping;
    }

    /**
     * Check if the host exists in the database and not in maintenance mode
     */
    private void validateHost(Map<String, List<String>> componentHostMapping) {
        for (Map.Entry<String, List<String>> entry : componentHostMapping.entrySet()) {
            List<String> hostnames = entry.getValue();
            List<Host> hostList = hostRepository.findAllByHostnameIn(hostnames);
            if (hostList.size() != hostnames.size()) {
                log.info("componentHostMapping: {}, hostList: {}", componentHostMapping, hostList);
                throw new ServerException("Can't find host in database");
            }
        }
    }

    /**
     * 生成最小命令单元
     */
    private List<ComponentCommandWrapper> createCommandWrapper(CommandDTO commandDTO) {
        Command command = commandDTO.getCommand();
        Long clusterId = commandDTO.getClusterId();

        List<ComponentCommandWrapper> componentCommandWrappers = new ArrayList<>();
        switch (commandDTO.getCommandLevel()) {
            case INTERNAL_SERVICE_INSTALL -> componentCommandWrappers = getCommandWrappersFromStack(commandDTO);
            case SERVICE -> {
                if (command == Command.INSTALL) {
                    componentCommandWrappers = getCommandWrappersFromStack(commandDTO);
                } else {
                    List<String> serviceNameList = commandDTO.getServiceCommands()
                            .stream()
                            .map(ServiceCommandDTO::getServiceName)
                            .toList();
                    List<Component> componentList = componentRepository.findAllByClusterIdAndServiceServiceNameIn(clusterId, serviceNameList);
                    for (Component component : componentList) {
                        ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(component.getComponentName(), command, component);
                        componentCommandWrappers.add(componentCommandWrapper);
                    }
                }

            }
            case COMPONENT -> {
                List<ComponentCommandDTO> componentCommands = commandDTO.getComponentCommands();
                List<String> componentNames = componentCommands.stream().map(ComponentCommandDTO::getComponentName).toList();

                List<Component> components = componentRepository.findAllByClusterIdAndComponentNameIn(clusterId, componentNames);
                for (Component component : components) {
                    ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(component.getComponentName(), command, component);
                    componentCommandWrappers.add(componentCommandWrapper);
                }
            }
            default -> log.warn("Unknown commandType: {}", commandDTO);
        }
        return componentCommandWrappers;
    }

    private List<ComponentCommandWrapper> getCommandWrappersFromStack(CommandDTO commandDTO) {
        Command command = commandDTO.getCommand();
        Long clusterId = commandDTO.getClusterId();
        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();
        List<ComponentCommandWrapper> componentCommandWrappers = new ArrayList<>();

        Map<String, ImmutablePair<StackDTO, List<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

        ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));
        Map<String, ServiceDTO> serviceNameToDTO = immutablePair.getRight()
                .stream()
                .collect(Collectors.toMap(ServiceDTO::getServiceName, Function.identity()));

        // Persist service, component and hostComponent metadata to database
        for (ServiceCommandDTO serviceCommand : commandDTO.getServiceCommands()) {
            ServiceDTO serviceDTO = serviceNameToDTO.get(serviceCommand.getServiceName());
            Service service = ServiceMapper.INSTANCE.fromDTO2Entity(serviceDTO, cluster);
            List<ComponentDTO> componentDTOList = serviceDTO.getComponents();
            for (ComponentDTO componentDTO : componentDTOList) {
                String componentName = componentDTO.getComponentName();
                Component component = ComponentMapper.INSTANCE.fromDTO2Entity(componentDTO, service, cluster);

                // Generate componentCommandWrapper
                ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(componentName, command, component);
                componentCommandWrappers.add(componentCommandWrapper);
            }
        }

        return componentCommandWrappers;
    }


    private Task createTask(Component component, String hostname, Command command, Job job, Stage stage, String
            customCommand) {
        Task task = new Task();

        task.setName(MessageFormat.format("{0} for {1} and {2}",
                CaseUtils.toCamelCase(command.name(), true), component.getDisplayName(), hostname));

        // Required fields
        task.setHostname(hostname);
        task.setCommand(command);
        task.setServiceName(component.getService().getServiceName());
        task.setStackName(component.getCluster().getStack().getStackName());
        task.setStackVersion(component.getCluster().getStack().getStackVersion());

        // Context fields
        task.setComponentName(component.getComponentName());
        task.setServiceUser(component.getService().getServiceUser());
        task.setServiceGroup(component.getService().getServiceGroup());
        task.setCluster(component.getCluster());
        task.setCustomCommands(component.getCustomCommands());
        task.setCustomCommand(customCommand);
        task.setCommandScript(component.getCommandScript());

        // extra fields
        task.setCluster(job.getCluster());
        task.setJob(job);
        task.setStage(stage);
        task.setState(JobState.PENDING);

        RequestMessage requestMessage = getMessage(component, hostname, command, customCommand);
        task.setContent(JsonUtils.writeAsString(requestMessage));

        task.setMessageId(requestMessage.getMessageId());

        return task;
    }

    private RequestMessage getMessage(Component component, String hostname, Command command, String
            customCommand) {
        CommandPayload commandPayload = getMessagePayload(component, hostname, command, customCommand);

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMessageType(MessageType.COMMAND);
        requestMessage.setHostname(hostname);

        requestMessage.setMessagePayload(JsonUtils.writeAsString(commandPayload));
        return requestMessage;
    }

    private CommandPayload getMessagePayload(Component component, String hostname, Command command, String
            customCommand) {
        CommandPayload commandPayload = new CommandPayload();
        commandPayload.setServiceName(component.getService().getServiceName());
        commandPayload.setCommand(command);
        commandPayload.setCustomCommand(customCommand);
        commandPayload.setServiceUser(component.getService().getServiceUser());
        commandPayload.setServiceGroup(component.getService().getServiceGroup());
        commandPayload.setStackName(component.getCluster().getStack().getStackName());
        commandPayload.setStackVersion(component.getCluster().getStack().getStackVersion());
        commandPayload.setRoot(component.getService().getCluster().getRoot());
        commandPayload.setComponentName(component.getComponentName());
        commandPayload.setHostname(hostname);

        try {
            List<CustomCommandInfo> customCommands = JsonUtils.readFromString(component.getCustomCommands(), new TypeReference<>() {
            });
            commandPayload.setCustomCommands(customCommands);
        } catch (Exception ignored) {
        }

        try {
            List<OSSpecificInfo> osSpecifics = JsonUtils.readFromString(component.getService().getOsSpecifics(), new TypeReference<>() {
            });
            commandPayload.setOsSpecifics(osSpecifics);
        } catch (Exception ignored) {
        }

        try {
            ScriptInfo commandScript = JsonUtils.readFromString(component.getCommandScript(), new TypeReference<>() {
            });
            commandPayload.setCommandScript(commandScript);
        } catch (Exception ignored) {
        }

        return commandPayload;
    }

}
