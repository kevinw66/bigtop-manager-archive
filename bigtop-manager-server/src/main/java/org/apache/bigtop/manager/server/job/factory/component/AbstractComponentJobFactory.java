package org.apache.bigtop.manager.server.job.factory.component;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.pojo.ScriptInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.job.factory.AbstractJobFactory;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.ComponentCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.apache.bigtop.manager.server.orm.repository.ComponentRepository;
import org.apache.bigtop.manager.server.stack.dag.ComponentCommandWrapper;
import org.apache.bigtop.manager.server.stack.dag.DAG;
import org.apache.bigtop.manager.server.stack.dag.DagGraphEdge;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.text.CaseUtils;
import org.springframework.beans.BeanUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractComponentJobFactory extends AbstractJobFactory {

    @Resource
    private ComponentRepository componentRepository;

    @Override
    protected List<Stage> createStagesAndTasks() {
        return createStages(this.getClass().getName());
    }

    protected List<Stage> createStages(String callbackClassName) {
        return this.createStages(callbackClassName, jobContext.getCommandDTO().getCommand());
    }

    protected List<Stage> createStages(String callbackClassName, Command command) {
        CommandDTO commandDTO = jobContext.getCommandDTO();
        String customCommand = commandDTO.getCustomCommand();

        List<ComponentCommandWrapper> sortedCommandWrappers = getSortedCommandWrappers(command);
        Map<String, List<String>> componentHostMapping = getComponentHostMapping(command);

        List<Stage> stages = new ArrayList<>();
        for (ComponentCommandWrapper componentCommandWrapper : sortedCommandWrappers) {
            String componentName = componentCommandWrapper.getComponentName();
            Component component = componentCommandWrapper.getComponent();
            if (component == null) {
                throw new ApiException(ApiExceptionEnum.COMPONENT_NOT_FOUND);
            }

            Stage stage = createStage(callbackClassName, componentCommandWrapper, component, command);
            stages.add(stage);

            // Generate task list
            List<Task> tasks = new ArrayList<>();
            List<String> hostnames = componentHostMapping.get(componentName);
            for (String hostname : hostnames) {
                Task task = createTask(component, hostname, command, customCommand);
                tasks.add(task);
            }

            stage.setTasks(tasks);
        }

        return stages;
    }

    protected Map<String, List<String>> getComponentHostMapping(Command command) {
        List<ComponentCommandDTO> componentCommands = jobContext.getCommandDTO().getComponentCommands();
        return componentCommands.stream().collect(Collectors.toMap(ComponentCommandDTO::getComponentName, ComponentCommandDTO::getHostnames));
    }

    protected List<Component> getComponents() {
        List<ComponentCommandDTO> componentCommands = jobContext.getCommandDTO().getComponentCommands();
        List<String> componentNames = componentCommands.stream().map(ComponentCommandDTO::getComponentName).toList();

        return componentRepository.findAllByClusterIdAndComponentNameIn(cluster.getId(), componentNames);
    }

    private List<ComponentCommandWrapper> getSortedCommandWrappers(Command command) {
        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();
        String fullStackName = StackUtils.fullStackName(stackName, stackVersion);

        DAG<String, ComponentCommandWrapper, DagGraphEdge> dag = StackUtils.getStackDagMap().get(fullStackName);
        try {
            Map<String, ComponentCommandWrapper> commandWrapperMap = getCommandWrappers(command).stream()
                    .collect(Collectors.toMap(ComponentCommandWrapper::toString, Function.identity()));

            List<String> orderedList = dag.topologicalSort();
            List<String> todoList = new ArrayList<>(commandWrapperMap.keySet());

            orderedList.retainAll(todoList);
            todoList.removeAll(orderedList);
            orderedList.addAll(todoList);

            return orderedList.stream().map(commandWrapperMap::get).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

    private List<ComponentCommandWrapper> getCommandWrappers(Command command) {
        return getComponents().stream()
                .map(component -> new ComponentCommandWrapper(component.getComponentName(), command, component))
                .collect(Collectors.toList());
    }

    private Stage createStage(String callbackClassName, ComponentCommandWrapper componentCommandWrapper, Component component, Command command) {
        Stage stage = new Stage();
        stage.setState(JobState.PENDING);
        stage.setName(componentCommandWrapper.toDisplayString());
        stage.setServiceName(component.getService().getServiceName());
        stage.setComponentName(component.getComponentName());

        // Set stage callback
        stage.setCallbackClassName(callbackClassName);

        if (command == jobContext.getCommandDTO().getCommand()) {
            stage.setPayload(JsonUtils.writeAsString(jobContext.getCommandDTO()));
        } else {
            CommandDTO commandDTO = new CommandDTO();
            BeanUtils.copyProperties(jobContext.getCommandDTO(), commandDTO);
            commandDTO.setCommand(command);
            stage.setPayload(JsonUtils.writeAsString(commandDTO));
        }

        return stage;
    }

    private Task createTask(Component component, String hostname, Command command, String customCommand) {
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
        task.setCustomCommands(component.getCustomCommands());
        task.setCustomCommand(customCommand);
        task.setCommandScript(component.getCommandScript());

        // extra fields
        task.setState(JobState.PENDING);

        RequestMessage requestMessage = getMessage(component, hostname, command, customCommand);
        task.setContent(JsonUtils.writeAsString(requestMessage));
        task.setMessageId(requestMessage.getMessageId());

        return task;
    }

    private RequestMessage getMessage(Component component, String hostname, Command command, String customCommand) {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMessageType(MessageType.COMMAND);
        requestMessage.setHostname(hostname);
        requestMessage.setMessagePayload(JsonUtils.writeAsString(getMessagePayload(component, hostname, command, customCommand)));

        return requestMessage;
    }

    private CommandPayload getMessagePayload(Component component, String hostname, Command command, String customCommand) {
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

        commandPayload.setCustomCommands(JsonUtils.readFromString(component.getCustomCommands(), new TypeReference<>() {}));
        commandPayload.setOsSpecifics(JsonUtils.readFromString(component.getService().getOsSpecifics(), new TypeReference<>() {}));
        commandPayload.setCommandScript(JsonUtils.readFromString(component.getCommandScript(), ScriptInfo.class));

        return commandPayload;
    }
}
