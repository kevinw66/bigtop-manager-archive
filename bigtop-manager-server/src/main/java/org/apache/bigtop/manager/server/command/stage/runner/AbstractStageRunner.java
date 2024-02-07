package org.apache.bigtop.manager.server.command.stage.runner;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.BaseCommandMessage;
import org.apache.bigtop.manager.common.message.type.HostCachePayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.common.enums.JobState;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.PropertyDTO;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.dao.entity.*;
import org.apache.bigtop.manager.dao.repository.*;
import org.apache.bigtop.manager.server.utils.StackConfigUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.apache.bigtop.manager.common.constants.Constants.*;

@Slf4j
public abstract class AbstractStageRunner implements StageRunner {

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private ServiceConfigMappingRepository serviceConfigMappingRepository;

    @Resource
    private RepoRepository repoRepository;

    @Resource
    private SettingRepository settingRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private ComponentRepository componentRepository;

    protected Stage stage;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void run() {
        beforeRun();

        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (Task task : stage.getTasks()) {
            beforeRunTask(task);

            // TODO temp code, need to remove this
            String content = task.getContent();
            CommandDTO commandDTO = JsonUtils.readFromString(task.getJob().getPayload(), CommandDTO.class);
            if (commandDTO.getCommandLevel() != CommandLevel.CLUSTER && stage.getName().equals(CACHE_STAGE_NAME)) {
                content = generatePayload(task);
            }
            // End temp code

            BaseCommandMessage message = JsonUtils.readFromString(content, RequestMessage.class);
            message.setTaskId(task.getId());
            message.setStageId(stage.getId());
            message.setJobId(stage.getJob().getId());

            futures.add(CompletableFuture.supplyAsync(() -> {
                ResultMessage res = SpringContextHolder.getServerWebSocket().sendMessage(task.getHostname(), message);

                log.info("Execute task {} completed: {}", task.getId(), res);
                boolean taskSuccess = res.getCode() == MessageConstants.SUCCESS_CODE;

                if (taskSuccess) {
                    onTaskSuccess(task);
                } else {
                    onTaskFailure(task);
                }

                return taskSuccess;
            }));
        }

        List<Boolean> taskResults = futures.stream().map((future) -> {
            try {
                return future.get(COMMAND_MESSAGE_RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("Error running task", e);
                return false;
            }
        }).toList();

        boolean allTaskSuccess = taskResults.stream().allMatch(Boolean::booleanValue);
        if (allTaskSuccess) {
            onSuccess();
        } else {
            onFailure();
        }
    }

    @Override
    public void beforeRun() {
        stage.setState(JobState.PROCESSING);
        stageRepository.save(stage);
    }

    @Override
    public void onSuccess() {
        stage.setState(JobState.SUCCESSFUL);
        stageRepository.save(stage);
    }

    @Override
    public void onFailure() {
        stage.setState(JobState.FAILED);
        stageRepository.save(stage);
    }

    @Override
    public void beforeRunTask(Task task) {
        task.setState(JobState.PROCESSING);
        taskRepository.save(task);
    }

    @Override
    public void onTaskSuccess(Task task) {
        task.setState(JobState.SUCCESSFUL);
        taskRepository.save(task);
    }

    @Override
    public void onTaskFailure(Task task) {
        task.setState(JobState.FAILED);
        taskRepository.save(task);
    }

    public String generatePayload(Task task) {
        Cluster cluster = task.getCluster();
        RequestMessage requestMessage = getMessage(cluster, task.getHostname());
        log.info("[generatePayload]-[HostCacheJobFactory-requestMessage]: {}", requestMessage);
        return JsonUtils.writeAsString(requestMessage);
    }

    public RequestMessage getMessage(Cluster cluster, String hostname) {
        ClusterInfo clusterInfo = new ClusterInfo();
        Map<String, ComponentInfo> componentInfoMap = new HashMap<>();
        Map<String, Map<String, Object>> serviceConfigMap = new HashMap<>();
        Map<String, Set<String>> hostMap = new HashMap<>();
        List<RepoInfo> repoList = new ArrayList<>();
        Map<String, Set<String>> userMap = new HashMap<>();
        Map<String, Object> settingsMap = new HashMap<>();


        Long clusterId = cluster.getId();

        String clusterName = cluster.getClusterName();
        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();

        List<Service> services = serviceRepository.findAllByClusterId(clusterId);
        List<ServiceConfigMapping> serviceConfigMappingList = serviceConfigMappingRepository.findAllGroupLastest(clusterId);
        List<HostComponent> hostComponents = hostComponentRepository.findAllByComponentClusterId(clusterId);
        List<Repo> repos = repoRepository.findAllByCluster(cluster);
        Iterable<Setting> settings = settingRepository.findAll();
        List<Host> hostList = hostRepository.findAllByClusterId(clusterId);

        //Wrapper clusterInfo for HostCacheMessage
        clusterInfo.setClusterName(clusterName);
        clusterInfo.setStackName(stackName);
        clusterInfo.setStackVersion(stackVersion);
        clusterInfo.setUserGroup(cluster.getUserGroup());
        clusterInfo.setRepoTemplate(cluster.getRepoTemplate());
        clusterInfo.setRoot(cluster.getRoot());
        clusterInfo.setPackages(Arrays.asList(cluster.getPackages().split(",")));

        //Wrapper serviceConfigMap for HostCacheMessage
        serviceConfigMappingList.forEach(scm -> {
            ServiceConfig sc = scm.getServiceConfig();
            List<PropertyDTO> properties = JsonUtils.readFromString(sc.getPropertiesJson(), new TypeReference<>() {
            });
            String configMapStr = JsonUtils.writeAsString(StackConfigUtils.extractConfigMap(properties));

            if (serviceConfigMap.containsKey(sc.getService().getServiceName())) {
                serviceConfigMap.get(sc.getService().getServiceName()).put(sc.getTypeName(), configMapStr);
            } else {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put(sc.getTypeName(), configMapStr);
                serviceConfigMap.put(sc.getService().getServiceName(), hashMap);
            }
        });

        //Wrapper hostMap for HostCacheMessage
        hostComponents.forEach(x -> {
            if (hostMap.containsKey(x.getComponent().getComponentName())) {
                hostMap.get(x.getComponent().getComponentName()).add(x.getHost().getHostname());
            } else {
                Set<String> set = new HashSet<>();
                set.add(x.getHost().getHostname());
                hostMap.put(x.getComponent().getComponentName(), set);
            }
            hostMap.get(x.getComponent().getComponentName()).add(x.getHost().getHostname());
        });

        Set<String> hostNameSet = hostList.stream().map(Host::getHostname).collect(Collectors.toSet());
        hostMap.put(ALL_HOST_KEY, hostNameSet);

        //Wrapper repoList for HostCacheMessage
        repos.forEach(repo -> {
            RepoInfo repoInfo = RepoMapper.INSTANCE.fromEntity2Message(repo);
            repoList.add(repoInfo);
        });

        //Wrapper userMap for HostCacheMessage
        services.forEach(x -> userMap.put(x.getServiceUser(), Set.of(x.getServiceGroup())));

        //Wrapper settings for HostCacheMessage
        settings.forEach(x -> settingsMap.put(x.getTypeName(), x.getConfigData()));

        //Wrapper componentInfoList for HostCacheMessage
        List<Component> componentList = componentRepository.findAll();
        componentList.forEach(c -> {
            ComponentInfo componentInfo = new ComponentInfo();
            componentInfo.setComponentName(c.getComponentName());
            componentInfo.setCommandScript(c.getCommandScript());
            componentInfo.setDisplayName(c.getDisplayName());
            componentInfo.setCategory(c.getCategory());
            componentInfo.setCustomCommands(c.getCustomCommands());
            componentInfo.setServiceName(c.getService().getServiceName());
            componentInfoMap.put(c.getComponentName(), componentInfo);
        });

        HostCachePayload hostCachePayload = new HostCachePayload();
        hostCachePayload.setHostname(hostname);

        hostCachePayload.setClusterInfo(clusterInfo);
        hostCachePayload.setConfigurations(serviceConfigMap);
        hostCachePayload.setClusterHostInfo(hostMap);
        hostCachePayload.setRepoInfo(repoList);
        hostCachePayload.setSettings(settingsMap);
        hostCachePayload.setUserInfo(userMap);
        hostCachePayload.setComponentInfo(componentInfoMap);

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMessageType(MessageType.HOST_CACHE);
        requestMessage.setHostname(hostname);
        requestMessage.setMessagePayload(JsonUtils.writeAsString(hostCachePayload));

        return requestMessage;
    }
}
