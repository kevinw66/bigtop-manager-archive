package org.apache.bigtop.manager.server.listener.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
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
import org.apache.bigtop.manager.server.enums.CommandType;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.stack.dag.ComponentCommandWrapper;
import org.apache.bigtop.manager.server.stack.dag.DAG;
import org.apache.bigtop.manager.server.stack.dag.DagGraphEdge;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Component
public class CommandJobFactory implements JobFactory {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private HostCacheJobFactory hostCacheJobFactory;

    /**
     * create job and persist it to database
     *
     * @param commandDTO command DTO
     * @return task flow queue
     */
    public Job createJob(CommandDTO commandDTO) {
        String clusterName = commandDTO.getClusterName();
        String stackName = commandDTO.getStackName();
        String stackVersion = commandDTO.getStackVersion();
        Command command = commandDTO.getCommand();
        String customCommand = commandDTO.getCustomCommand();

        Job job = new Job();
        job.setState(JobState.PENDING);
        job.setContext(commandDTO.toString());
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());
        job.setCluster(cluster);
        job = jobRepository.save(job);
        log.info("CommandOperator-job: {}", job);

        List<ComponentCommandWrapper> componentCommandWrappers = createCommandWrapper(commandDTO);
        List<ComponentCommandWrapper> sortedRcpList = stageSort(componentCommandWrappers, stackName, stackVersion);
        Map<String, Set<String>> componentHostMapping = createComponentHostMapping(sortedRcpList, commandDTO);


        int startOrder = 0;
        if (commandDTO.getCommandType() == CommandType.HOST_INSTALL || commandDTO.getCommandType() == CommandType.SERVICE_INSTALL) {
            startOrder = 1;
            hostCacheJobFactory.createStage(job, cluster, startOrder);
        }

        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < sortedRcpList.size(); i++) {
            ComponentCommandWrapper componentCommandWrapper = sortedRcpList.get(i);
            String componentName = componentCommandWrapper.getComponentName();

            Stage stage = new Stage();
            stage.setJob(job);
            stage.setCluster(job.getCluster());
            stage.setState(JobState.PENDING);
            stage.setName(componentCommandWrapper.toString());
            stage.setStageOrder(i + 1 + startOrder);
            stage = stageRepository.save(stage);
            log.info("stage: {}", stage);

            Set<String> hostSet = componentHostMapping.get(componentName);
            // Query host component table, obtain the host list for this role
            Component component = componentRepository.findByClusterClusterNameAndComponentName(clusterName, componentName).orElse(new Component());
            // Generate task list
            if (component.getId() != null) {
                for (String hostname : hostSet) {
                    Task task = createTask(component, hostname, command, job, stage, customCommand);
                    log.info("task: {}", task);
                    tasks.add(task);
                }
            }
        }
        taskRepository.saveAll(tasks);

        if (commandDTO.getCommandType() == CommandType.HOST_INSTALL || commandDTO.getCommandType() == CommandType.SERVICE_INSTALL) {
            hostCacheJobFactory.createStage(job, cluster, startOrder + sortedRcpList.size() + 1);
        }
        return job;
    }


    /**
     * Execute tasks based on DAG serial sorting
     * Can be optimized for parallel execution
     *
     * @param todoList     request list
     * @param stackName    stack name
     * @param stackVersion stack version
     */
    private List<ComponentCommandWrapper> stageSort(List<ComponentCommandWrapper> todoList, String stackName, String stackVersion) {
        List<ComponentCommandWrapper> sortedList = new ArrayList<>();

        DAG<String, ComponentCommandWrapper, DagGraphEdge> dag = StackUtils.getStackDagMap().get(StackUtils.fullStackName(stackName, stackVersion));

        try {
            List<String> orderedList = dag.topologicalSort();
            log.info("DAG topological sort list: {}", orderedList);

            for (String node : orderedList) {
                ComponentCommandWrapper nodeInfo = dag.getNode(node);
                if (todoList.contains(nodeInfo)) {
                    sortedList.add(nodeInfo);
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
     * @return componentHostMapping key: component name, value: host list. e.g. {ZOOKEEPER_SERVER=[node1], KAFKA_SERVER=[node1, node2]}
     */
    private Map<String, Set<String>> createComponentHostMapping(List<ComponentCommandWrapper> sortedRcpList, CommandDTO commandDTO) throws ApiException {
        Map<String, Set<String>> componentHostMapping = new HashMap<>();

        String clusterName = commandDTO.getClusterName();

        switch (commandDTO.getCommandType()) {
            case HOST, HOST_INSTALL -> {
                String hostname = commandDTO.getHostname();
                for (ComponentCommandWrapper componentCommandWrapper : sortedRcpList) {
                    Set<String> hostSet = Sets.newHashSet(hostname);
                    componentHostMapping.put(componentCommandWrapper.getComponentName(), hostSet);
                }
            }
            case SERVICE_INSTALL -> componentHostMapping = commandDTO.getComponentHosts();
            case CLUSTER, SERVICE, COMPONENT -> {
                for (ComponentCommandWrapper componentCommandWrapper : sortedRcpList) {
                    String componentName = componentCommandWrapper.getComponentName();
                    List<HostComponent> hostComponentList = hostComponentRepository.findAllByComponentClusterClusterNameAndComponentComponentName(clusterName, componentName);

                    Set<String> hostSet = hostComponentList.stream().map(x -> x.getHost().getHostname()).collect(Collectors.toSet());

                    componentHostMapping.put(componentName, hostSet);
                }
            }
            default -> log.warn("Unknown commandType: {}", commandDTO);
        }

        validateHost(componentHostMapping);

        return componentHostMapping;
    }

    /**
     * Check if the host exists in the database and not in maintenance mode
     */
    private void validateHost(Map<String, Set<String>> componentHostMapping) {
        for (Map.Entry<String, Set<String>> entry : componentHostMapping.entrySet()) {
            Set<String> hostSet = entry.getValue();
            List<Host> hostList = hostRepository.findAllByHostnameIn(hostSet);
            if (hostList.size() != hostSet.size()) {
                throw new ServerException("Can't find host in database");
            }
        }
    }

    /**
     * 生成最小命令单元
     */
    private List<ComponentCommandWrapper> createCommandWrapper(CommandDTO commandDTO) {
        String clusterName = commandDTO.getClusterName();
        Command command = commandDTO.getCommand();
        String stackName = commandDTO.getStackName();
        String stackVersion = commandDTO.getStackVersion();

        List<ComponentCommandWrapper> componentCommandWrappers = new ArrayList<>();
        switch (commandDTO.getCommandType()) {
            case SERVICE -> {
                List<String> serviceNameList = commandDTO.getServiceNames();
                List<Component> componentList = componentRepository.findAllByClusterClusterNameAndServiceServiceNameIn(clusterName, serviceNameList);
                for (Component component : componentList) {
                    ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(component.getComponentName(), command);
                    componentCommandWrappers.add(componentCommandWrapper);
                }
            }
            case COMPONENT, HOST, HOST_INSTALL -> {
                for (String componentName : commandDTO.getComponentNames()) {
                    ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(componentName, command);
                    componentCommandWrappers.add(componentCommandWrapper);
                }
            }
            case SERVICE_INSTALL -> {
                List<String> serviceNameList = commandDTO.getServiceNames();
                Map<String, ImmutablePair<StackDTO, List<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

                ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));
                List<ServiceDTO> serviceDTOSet = immutablePair.getRight();

                // Persist service, component and hostComponent metadata to database
                for (ServiceDTO serviceDTO : serviceDTOSet) {
                    String serviceName = serviceDTO.getServiceName();

                    if (serviceNameList.contains(serviceName)) {
                        List<ComponentDTO> componentDTOList = serviceDTO.getComponents();
                        for (ComponentDTO componentDTO : componentDTOList) {
                            String componentName = componentDTO.getComponentName();
                            // Generate componentCommandWrapper
                            ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(componentName, command);
                            componentCommandWrappers.add(componentCommandWrapper);
                        }
                    }
                }
            }
            case CLUSTER -> {
                List<Component> componentList = componentRepository.findAllByClusterClusterName(clusterName);
                for (Component component : componentList) {
                    ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(component.getComponentName(), command);
                    componentCommandWrappers.add(componentCommandWrapper);
                }
            }
            default -> log.warn("Unknown commandType: {}", commandDTO);
        }
        return componentCommandWrappers;
    }


    private Task createTask(Component component, String hostname, Command command, Job job, Stage stage, String customCommand) {
        Task task = new Task();

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

    private RequestMessage getMessage(Component component, String hostname, Command command, String customCommand) {
        CommandPayload commandPayload = getMessagePayload(component, hostname, command, customCommand);

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMessageType(MessageType.COMMAND);
        requestMessage.setHostname(hostname);

        requestMessage.setMessagePayload(JsonUtils.writeAsString(commandPayload));
        return requestMessage;
    }

    private CommandPayload getMessagePayload(Component component, String hostname, Command command, String customCommand) {
        CommandPayload commandMessage = new CommandPayload();
        commandMessage.setServiceName(component.getService().getServiceName());
        commandMessage.setCommand(command);
        commandMessage.setCustomCommand(customCommand);
        commandMessage.setServiceUser(component.getService().getServiceUser());
        commandMessage.setServiceGroup(component.getService().getServiceGroup());
        commandMessage.setStackName(component.getCluster().getStack().getStackName());
        commandMessage.setStackVersion(component.getCluster().getStack().getStackVersion());
        commandMessage.setRoot(component.getService().getCluster().getRoot());
        commandMessage.setComponentName(component.getComponentName());
        commandMessage.setHostname(hostname);

        try {
            List<CustomCommandInfo> customCommands = JsonUtils.readFromString(component.getCustomCommands(), new TypeReference<>() {
            });
            commandMessage.setCustomCommands(customCommands);
        } catch (Exception ignored) {
        }

        try {
            List<OSSpecificInfo> osSpecifics = JsonUtils.readFromString(component.getService().getOsSpecifics(), new TypeReference<>() {
            });
            commandMessage.setOsSpecifics(osSpecifics);
        } catch (Exception ignored) {
        }

        try {
            ScriptInfo commandScript = JsonUtils.readFromString(component.getCommandScript(), new TypeReference<>() {
            });
            commandMessage.setCommandScript(commandScript);
        } catch (Exception ignored) {
        }

        return commandMessage;
    }

}
