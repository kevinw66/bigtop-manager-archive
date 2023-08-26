package org.apache.bigtop.manager.server.ws;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandEvent;
import org.apache.bigtop.manager.server.enums.CommandState;
import org.apache.bigtop.manager.server.enums.RequestState;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.repository.ComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.bigtop.manager.server.orm.repository.RequestRepository;
import org.apache.bigtop.manager.server.stack.dag.*;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@org.springframework.stereotype.Component
public class TaskFlowHandler implements Callback {

    @Getter
    private final Map<String, List<Task>> displayTaskFlow = new TreeMap<>();

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private RequestRepository requestRepository;

    @Resource
    private ServerWebSocketHandler serverWebSocketHandler;

    /**
     * Execute tasks based on DAG serial sorting
     * Can be optimized for parallel execution
     *
     * @param todoList request list
     * @param stackName stack name
     * @param stackVersion stack version
     */
    public List<ComponentCommandWrapper> generateProcessWrapper(List<ComponentCommandWrapper> todoList, String stackName, String stackVersion) {

        List<ComponentCommandWrapper> sortedList = new ArrayList<>();

        String fullStackName = StackUtils.fullStackName(stackName, stackVersion);
        DAG<ComponentCommandWrapper, ComponentCommandWrapper, DagGraphEdge> mergedDependencyMap = DagHelper.getStackDagMap().get(fullStackName);


        for (ComponentCommandWrapper roleCommandPair : todoList) {
            Map<ComponentCommandWrapper, Map<ComponentCommandWrapper, DagGraphEdge>> reverseEdgesMap = mergedDependencyMap.getReverseEdgesMap();

            Map<ComponentCommandWrapper, DagGraphEdge> blockerMap = reverseEdgesMap.get(roleCommandPair);

            // add dependencies
            for (ComponentCommandWrapper commandPair : todoList) {

                if (MapUtils.isNotEmpty(blockerMap) && blockerMap.containsKey(commandPair) && !sortedList.contains(commandPair)) {
                    sortedList.add(commandPair);
                }
            }
            if (!sortedList.contains(roleCommandPair)) {
                sortedList.add(roleCommandPair);
            }
        }
        log.info("sortedList: {}", sortedList);
        return sortedList;
    }

    public Map<String, Set<String>> getComponentHostMapping(List<ComponentCommandWrapper> sortedRcpList,
                                                            CommandDTO commandDTO) throws ServerException{
        Map<String, Set<String>> componentHostMapping = new HashMap<>();

        String clusterName = commandDTO.getClusterName();

        switch (commandDTO.getCommandType()) {
            case HOST -> {
                String hostname = commandDTO.getHostname();
                for (ComponentCommandWrapper componentCommandWrapper : sortedRcpList) {
                    Set<String> hostSet = new HashSet<>();
                    hostSet.add(hostname);
                    componentHostMapping.put(componentCommandWrapper.getComponentName(), hostSet);
                }
            }
            case INSTALL_SERVICE -> componentHostMapping = commandDTO.getComponentHosts();
            case CLUSTER, SERVICE, COMPONENT -> {
                for (ComponentCommandWrapper componentCommandWrapper : sortedRcpList) {
                    String componentName = componentCommandWrapper.getComponentName();
                    List<HostComponent> hostComponentList = hostComponentRepository
                            .findAllByComponentClusterClusterNameAndComponentComponentName(clusterName, componentName);

                    Set<String> hostSet = new HashSet<>();
                    for (HostComponent hostComponent : hostComponentList) {
                        hostSet.add(hostComponent.getHost().getHostname());
                    }
                    componentHostMapping.put(componentName, hostSet);
                }
            }
            default -> log.warn("Unknown commandType: {}", commandDTO);
        }

        //Check
        for (Map.Entry<String, Set<String>> entry : componentHostMapping.entrySet()) {
            Set<String> hostSet = entry.getValue();
            List<Host> hostList = hostRepository.findAllByHostnameIn(hostSet);
            if (hostList.size() != hostSet.size()) {
                log.error("Can't find host in database");
                throw new ServerException("Can't find host in database");
            }

        }

        return componentHostMapping;
    }

    /**
     *
     * @param commandDTO
     * componentHostMapping key: component name, value: host list
     * @return task flow
     */
    public Queue<List<Task>> generateTaskFlow(CommandDTO commandDTO) {
        String clusterName = commandDTO.getClusterName();
        String stackName = commandDTO.getStackName();
        String stackVersion = commandDTO.getStackVersion();
        String command = commandDTO.getCommand();

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
            case COMPONENT, HOST -> {
                for (String componentName : commandDTO.getComponentNames()) {
                    ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(componentName, command);
                    componentCommandWrappers.add(componentCommandWrapper);
                }
            }
            case INSTALL_SERVICE -> {
                List<String> serviceNameList = commandDTO.getServiceNames();
                Map<String, ImmutablePair<StackDTO, Set<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

                ImmutablePair<StackDTO, Set<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));
                Set<ServiceDTO> serviceDTOSet = immutablePair.getRight();

                // Persist service, component and hostComponent metadata to database
                for (ServiceDTO serviceDTO : serviceDTOSet) {
                    String serviceName = serviceDTO.getServiceName();

                    if (serviceNameList.contains(serviceName)) {
                        List<ComponentDTO> componentDTOList = serviceDTO.getComponents();
                        for (ComponentDTO componentDTO : componentDTOList) {
                            String componentName = componentDTO.getComponentName();
                            //generate componentCommandWrapper
                            ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(componentName, CommandEvent.INSTALL.name());
                            componentCommandWrappers.add(componentCommandWrapper);
                        }
                    }
                }
            }
            case CLUSTER -> {
                List<Component> componentList = componentRepository.findByClusterClusterName(clusterName);
                for (Component component : componentList) {
                    ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(component.getComponentName(), command);
                    componentCommandWrappers.add(componentCommandWrapper);
                }
            }
            default -> log.warn("Unknown commandType: {}", commandDTO);
        }


        List<ComponentCommandWrapper> sortedRcpList = generateProcessWrapper(componentCommandWrappers, stackName, stackVersion);
        Map<String, Set<String>> componentHostMapping = getComponentHostMapping(sortedRcpList, commandDTO);

        Queue<List<Task>> taskFlow = new LinkedList<>();
        displayTaskFlow.clear();

        for (ComponentCommandWrapper componentCommandWrapper : sortedRcpList) {
            String componentName = componentCommandWrapper.getComponentName();

            List<Task> taskList = new ArrayList<>();

            Set<String> hostSet = componentHostMapping.get(componentName);
            //Query host_component table, obtain the host list for this role
            Component component = componentRepository.findByClusterClusterNameAndComponentName(clusterName, componentName)
                    .orElse(new Component());
            //generate task list
            if (component.getId() != null) {
                for (String hostname : hostSet) {

                    Task task = convertTask(component, hostname, command);

                    taskList.add(task);

                    //Map of task flow for display fronted
                    displayTaskFlow.computeIfAbsent(hostname, key -> new ArrayList<>()).add(task);

                }
            }
            //Unable to find the corresponding host for the component, no task will be generated
            if (!taskList.isEmpty()) {
                taskFlow.offer(taskList);
            }
        }

        return taskFlow;
    }


    private Task convertTask(Component component, String hostname, String command) {
        Task task = new Task();
        task.setTaskId(UUID.randomUUID().toString().replace("-", ""));
        //Required fields
        task.setHostname(hostname);
        task.setCommand(command);
        task.setScriptId(component.getScriptId());
        task.setRoot(component.getCluster().getRoot());
        task.setCacheDir(component.getCluster().getCacheDir());
        task.setServiceName(component.getService().getServiceName());
        task.setStackName(component.getCluster().getStack().getStackName());
        task.setStackVersion(component.getCluster().getStack().getStackVersion());
        //Context fields
        task.setComponentName(component.getComponentName());
        task.setServiceUser(component.getService().getServiceUser());
        task.setServiceGroup(component.getService().getServiceGroup());
        task.setOsSpecifics(component.getService().getOsSpecifics());
        log.debug("convertTask() task: {}", task);
        return task;
    }

    public void submitTaskFlow(CommandDTO commandDTO) {
        this.generatedTaskFlow = generateTaskFlow(commandDTO);

        List<Task> taskList;
        taskStatusMap.clear();

        while (!generatedTaskFlow.isEmpty()) {
            taskList = generatedTaskFlow.poll();
            log.info("starting execute task flow");

            for (Task task : taskList) {
                String hostname = task.getHostname();
                CommandMessage commandMessage = new CommandMessage();
                commandMessage.setCommand(task.getCommand());
                commandMessage.setScriptId(task.getScriptId());
                commandMessage.setRoot(task.getRoot());
                commandMessage.setCacheDir(task.getCacheDir());
                commandMessage.setHostname(hostname);
                commandMessage.setService(task.getServiceName());
                commandMessage.setStack(task.getStackName());
                commandMessage.setVersion(task.getStackVersion());

                List<OSSpecificInfo> osSpecifics = JsonUtils.string2Json(task.getOsSpecifics(), new TypeReference<>() {
                });
                commandMessage.setOsSpecifics(osSpecifics);

                commandMessage.setServiceUser(task.getServiceUser());
                commandMessage.setServiceGroup(task.getServiceGroup());

                commandMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
                commandMessage.setMessageId(getMessageId(task));

                log.debug("commandMessage: {}", commandMessage);
                serverWebSocketHandler.sendMessage(hostname, commandMessage, this);
                taskStatusMap.put(getMessageId(task), task);
            }

        }

    }

    private final ConcurrentHashMap<String, Task> taskStatusMap = new ConcurrentHashMap<>();

    private Queue<List<Task>> generatedTaskFlow;

    @Override
    public void call(ResultMessage resultMessage) {
        log.info("execute command completed");

        String messageId = resultMessage.getMessageId();
        Task task = taskStatusMap.get(messageId);
        String componentName = task.getComponentName();
        String hostname = task.getHostname();
        String command = task.getCommand();

        //TODO: Send success or failure messages to the frontend
        if (resultMessage.getCode() == 0) {
            System.out.println("执行成功");
            task.setState(RequestState.SUCCESSFUL);

            Optional<HostComponent> hostComponentOptional = hostComponentRepository.findByComponentComponentNameAndHostHostname(componentName, hostname);
            HostComponent hostComponent = hostComponentOptional.get();
            switch (CommandEvent.valueOf(command)) {
                case INSTALL -> hostComponent.setState(CommandState.INSTALLED.name());
                case START -> hostComponent.setState(CommandState.STARTED.name());
                case STOP -> hostComponent.setState(CommandState.INSTALLED.name());
            }
            hostComponentRepository.save(hostComponent);

        } else {
            System.out.println("执行失败");
            task.setState(RequestState.FAILED);
            //当前任务状态更新为失败
            //弹出后续所有任务并赋予取消状态
            while (!generatedTaskFlow.isEmpty()) {
                List<Task> taskList = generatedTaskFlow.poll();
                taskList.forEach(t -> {
                    String msgId = getMessageId(t);
                    task.setState(RequestState.CANCELED);
                    System.out.println("任务取消： " + msgId);
                });
            }

        }

    }

    private String getMessageId(Task task) {
        return task.getTaskId() + "_" + task.getComponentName() + "_" + task.getCommand() + "_" + task.getHostname();
    }
}
