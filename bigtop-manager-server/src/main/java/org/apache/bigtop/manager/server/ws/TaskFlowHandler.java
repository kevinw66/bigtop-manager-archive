package org.apache.bigtop.manager.server.ws;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.enums.StatusType;
import org.apache.bigtop.manager.server.enums.heartbeat.CommandState;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.stack.dag.ComponentCommandWrapper;
import org.apache.bigtop.manager.server.stack.dag.DAG;
import org.apache.bigtop.manager.server.stack.dag.DagGraphEdge;
import org.apache.bigtop.manager.server.stack.dag.DagHelper;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * job -> stage -> task
 */
@Slf4j
@org.springframework.stereotype.Component
public class TaskFlowHandler implements Callback {

    @Resource
    private AsyncEventBus asyncEventBus;

    @PostConstruct
    public void init() {
        asyncEventBus.register(this);
    }

    @Getter
    private final Map<String, List<Task>> displayTaskFlow = new TreeMap<>();

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

    /**
     * Fill in the mapping of components and hosts
     *
     * @param sortedRcpList result of {@link TaskFlowHandler#generateProcessWrapper}
     * @param commandEvent command event
     * @return componentHostMapping key: component name, value: host list. e.g. {ZOOKEEPER_SERVER=[node1], KAFKA_SERVER=[node1, node2]}
     */
    public Map<String, Set<String>> getComponentHostMapping(List<ComponentCommandWrapper> sortedRcpList,
                                                            CommandEvent commandEvent) throws ServerException {
        Map<String, Set<String>> componentHostMapping = new HashMap<>();

        String clusterName = commandEvent.getClusterName();

        switch (commandEvent.getCommandType()) {
            case HOST -> {
                String hostname = commandEvent.getHostname();
                for (ComponentCommandWrapper componentCommandWrapper : sortedRcpList) {
                    Set<String> hostSet = new HashSet<>();
                    hostSet.add(hostname);
                    componentHostMapping.put(componentCommandWrapper.getComponentName(), hostSet);
                }
            }
            case INSTALL_SERVICE -> componentHostMapping = commandEvent.getComponentHosts();
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
            default -> log.warn("Unknown commandType: {}", commandEvent);
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
     * componentHostMapping key: component name, value: host list
     * @param commandEvent command event
     * @return task flow queue
     */
    public BlockingQueue<Stage> generateTaskFlow(CommandEvent commandEvent) {
        String clusterName = commandEvent.getClusterName();
        String stackName = commandEvent.getStackName();
        String stackVersion = commandEvent.getStackVersion();
        Command command = commandEvent.getCommand();
        Long jobId = commandEvent.getJobId();

        List<ComponentCommandWrapper> componentCommandWrappers = new ArrayList<>();
        switch (commandEvent.getCommandType()) {
            case SERVICE -> {
                List<String> serviceNameList = commandEvent.getServiceNames();
                List<Component> componentList = componentRepository.findAllByClusterClusterNameAndServiceServiceNameIn(clusterName, serviceNameList);
                for (Component component : componentList) {
                    ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(component.getComponentName(), command);
                    componentCommandWrappers.add(componentCommandWrapper);
                }
            }
            case COMPONENT, HOST -> {
                for (String componentName : commandEvent.getComponentNames()) {
                    ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(componentName, command);
                    componentCommandWrappers.add(componentCommandWrapper);
                }
            }
            case INSTALL_SERVICE -> {
                List<String> serviceNameList = commandEvent.getServiceNames();
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
                            ComponentCommandWrapper componentCommandWrapper = new ComponentCommandWrapper(componentName, Command.INSTALL);
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
            default -> log.warn("Unknown commandType: {}", commandEvent);
        }


        List<ComponentCommandWrapper> sortedRcpList = generateProcessWrapper(componentCommandWrappers, stackName, stackVersion);
        Map<String, Set<String>> componentHostMapping = getComponentHostMapping(sortedRcpList, commandEvent);

        BlockingQueue<Stage> taskFlowQueue = new LinkedBlockingQueue<>();
        displayTaskFlow.clear();

        Job job = jobRepository.findById(jobId).orElse(new Job());

        for (ComponentCommandWrapper componentCommandWrapper : sortedRcpList) {
            String componentName = componentCommandWrapper.getComponentName();

            Stage stage = new Stage();
            stage.setJob(job);
            stage.setCluster(job.getCluster());
            stage.setState(JobState.PENDING);
            stage = stageRepository.save(stage);

            Set<String> hostSet = componentHostMapping.get(componentName);
            //Query host_component table, obtain the host list for this role
            Component component = componentRepository.findByClusterClusterNameAndComponentName(clusterName, componentName).orElse(new Component());
            //generate task list
            if (component.getId() != null) {
                for (String hostname : hostSet) {
                    Task task = convertTask(component, hostname, command, job, stage);
                    stage.getTasks().add(task);
                    //Map of task flow for display frontend
                    displayTaskFlow.computeIfAbsent(hostname, key -> new ArrayList<>()).add(task);
                }
            }

            taskFlowQueue.offer(stage);
        }

        return taskFlowQueue;
    }


    private Task convertTask(Component component, String hostname, Command command, Job job, Stage stage) {
        Task task = new Task();
        //Required fields
        task.setHostname(hostname);
        task.setCommand(command);
        task.setScriptId(component.getScriptId());
        task.setRoot(component.getCluster().getRoot());
        task.setServiceName(component.getService().getServiceName());
        task.setStackName(component.getCluster().getStack().getStackName());
        task.setStackVersion(component.getCluster().getStack().getStackVersion());
        //Context fields
        task.setComponentName(component.getComponentName());
        task.setServiceUser(component.getService().getServiceUser());
        task.setServiceGroup(component.getService().getServiceGroup());
        task.setOsSpecifics(component.getService().getOsSpecifics());
        task.setCluster(component.getCluster());
        log.debug("convertTask() task: {}", task);

        task.setCluster(job.getCluster());
        task.setJob(job);
        task.setStage(stage);
        task.setState(JobState.PENDING);
        task = taskRepository.save(task);

        return task;
    }

    private CommandMessage convertCommandMassage(Task task) {
        CommandMessage commandMessage = new CommandMessage();

        commandMessage.setCommand(task.getCommand());
        commandMessage.setScriptId(task.getScriptId());
        commandMessage.setRoot(task.getRoot());
        commandMessage.setHostname(task.getHostname());
        commandMessage.setServiceName(task.getServiceName());
        commandMessage.setStackName(task.getStackName());
        commandMessage.setStackVersion(task.getStackVersion());

        try {
            List<OSSpecificInfo> osSpecifics = JsonUtils.readFromString(task.getOsSpecifics(), new TypeReference<>() {
            });
            commandMessage.setOsSpecifics(osSpecifics);
        } catch (Exception ignored) {
        }

        commandMessage.setServiceUser(task.getServiceUser());
        commandMessage.setServiceGroup(task.getServiceGroup());

        // requestId stageId taskId
        commandMessage.setJobId(task.getJob().getId());
        commandMessage.setStageId(task.getStage().getId());
        commandMessage.setTaskId(task.getId());

        return commandMessage;
    }

    @Subscribe
    public void submitTaskFlow(CommandEvent commandEvent) {
        this.taskFlowQueue = generateTaskFlow(commandEvent);

        // job state is processing
        Long jobId = commandEvent.getJobId();
        Job job = jobRepository.findById(jobId).orElse(new Job());
        job.setState(JobState.PROCESSING);
        jobRepository.save(job);

        failedList.clear();

        while (!taskFlowQueue.isEmpty()) {
            Stage stage = taskFlowQueue.poll();
            log.info("starting execute task flow");

            // stage state is processing
            stage.setState(JobState.PROCESSING);
            stageRepository.save(stage);

            for (Task task : stage.getTasks()) {
                CommandMessage commandMessage = convertCommandMassage(task);
                if (log.isDebugEnabled()) {
                    log.debug("commandMessage: {}", commandMessage);
                }
                serverWebSocketHandler.sendMessage(task.getHostname(), commandMessage, this);

                // task state is processing
                task.setMessageId(commandMessage.getMessageId());
                task.setState(JobState.PROCESSING);
                taskRepository.save(task);
            }

            countDownLatch = new CountDownLatch(stage.getTasks().size());

            try {
                boolean timeoutFlag = countDownLatch.await(5 * 60 * 1000, TimeUnit.MILLISECONDS);
                if (!timeoutFlag) {
                    log.error("execute task timeout");
                    stage.setState(JobState.TIMEOUT);
                    stageRepository.save(stage);
                    job.setState(JobState.TIMEOUT);
                    jobRepository.save(job);

                    releaseRemainStages();
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (!failedList.contains(new ImmutablePair<>(jobId, stage.getId()))) {
                stage.setState(JobState.SUCCESSFUL);
                stageRepository.save(stage);
            }
        }

        if (failedList.isEmpty()) {
            job.setState(JobState.SUCCESSFUL);
            jobRepository.save(job);
        }
    }

    private final List<ImmutablePair<Long, Long>> failedList = new ArrayList<>();

    private BlockingQueue<Stage> taskFlowQueue;

    private CountDownLatch countDownLatch;

    @Override
    public void call(ResultMessage resultMessage) {
        log.info("execute command completed");
        countDownLatch.countDown();

        Task task = taskRepository.findById(resultMessage.getTaskId()).orElse(new Task());

        String componentName = task.getComponentName();
        String hostname = task.getHostname();
        Command command = task.getCommand();

        //TODO: Send success or failure messages to the frontend
        if (resultMessage.getCode() == MessageConstants.SUCCESS_CODE) {
            log.info("Execute Task SUCCESSFUL. taskId: {}", task.getId());
            task.setState(JobState.SUCCESSFUL);
            taskRepository.save(task);

            HostComponent hostComponent = hostComponentRepository.findByComponentComponentNameAndHostHostname(componentName, hostname)
                    .orElse(new HostComponent());
            switch (command) {
                case INSTALL -> {
                    hostComponent.setStatus(StatusType.INSTALLED.getCode());
                    hostComponent.setState(CommandState.INSTALLED);
                }
                case START -> hostComponent.setState(CommandState.STARTED);
                case STOP -> hostComponent.setState(CommandState.STOPPED);
            }
            hostComponentRepository.save(hostComponent);

        } else {
            log.info("Execute Task FAILED, Cancel other stages. taskId: {}", task.getId());
            Stage stage = task.getStage();
            stage.setState(JobState.FAILED);
            stageRepository.save(stage);
            Job job = task.getJob();
            job.setState(JobState.FAILED);
            jobRepository.save(job);

            failedList.add(new ImmutablePair<>(job.getId(), stage.getId()));

            //Updating the current task status to FAILED
            //Pop up all subsequent tasks and assign CANCELED status
            releaseRemainStages();
        }

        ImmutablePair<Float, Float> progress = getProgress(task);
        log.info("job progress: {}, job-host progress: {}", progress.getLeft(), progress.getRight());

    }

    /**
     * Release remaining Stages
     */
    private void releaseRemainStages() {
        if (!taskFlowQueue.isEmpty()) {
            List<Stage> remainStages = new ArrayList<>(taskFlowQueue.size());
            taskFlowQueue.drainTo(remainStages);
            for (Stage s : remainStages) {
                //Setting the status of the remaining stages to CANCELED
                s.setState(JobState.CANCELED);
                stageRepository.save(s);

                s.getTasks().forEach(t -> t.setState(JobState.CANCELED));
                taskRepository.saveAll(s.getTasks());
            }
        }
    }

    /**
     * Calculate execution progress
     */
    private ImmutablePair<Float, Float> getProgress(Task task) {
        List<Task> totalTaskList = taskRepository.findAllByJobId(task.getJob().getId());
        int totalTaskSize = totalTaskList.size();

        List<Task> completedTaskList = taskRepository.findAllByJobIdAndState(task.getJob().getId(), JobState.SUCCESSFUL);

        log.info("completedTaskList.size(), i: {}, {}", completedTaskList.size(), totalTaskSize);
        Float jobProgress = (float) completedTaskList.size() / (float) totalTaskSize;

        String hostname = task.getHostname();
        List<Task> hostTaskList = displayTaskFlow.get(hostname);
        List<Task> hostCompletedTaskList = taskRepository.findAllByJobIdAndHostnameAndState(task.getJob().getId(), hostname, JobState.SUCCESSFUL);
        log.info("hostCompletedTaskList.size(), hostTaskList.size(): {}, {}", hostCompletedTaskList.size(), hostTaskList.size());
        Float jobHostProgress = (float) hostCompletedTaskList.size() / (float) hostTaskList.size();

        return new ImmutablePair<>(jobProgress, jobHostProgress);
    }

}
