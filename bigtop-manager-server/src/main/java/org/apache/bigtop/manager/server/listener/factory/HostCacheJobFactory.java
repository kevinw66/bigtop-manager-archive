package org.apache.bigtop.manager.server.listener.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.HostCachePayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.listener.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.PropertyDTO;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.utils.StackConfigUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.bigtop.manager.common.constants.Constants.ALL_HOST_KEY;

@Slf4j
@org.springframework.stereotype.Component
public class HostCacheJobFactory implements JobFactory, StageCallback {

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ClusterRepository clusterRepository;

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

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    public Job createJob(Long clusterId) {
        Job job = new Job();

        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        // Create job
        job.setContext("Cache Hosts");
        job.setState(JobState.PENDING);
        job.setCluster(cluster);
        job = jobRepository.save(job);

        // Create stages
        createStage(job, cluster, 1);

        return job;
    }

    public void createStage(Job job, Cluster cluster, int stageOrder) {
        createCache(cluster);

        List<Host> hostList = hostRepository.findAllByClusterId(cluster.getId());
        List<String> hostnames = hostList.stream().map(Host::getHostname).toList();

        Stage hostCacheStage = new Stage();
        hostCacheStage.setJob(job);
        hostCacheStage.setName("Cache Host");
        hostCacheStage.setState(JobState.PENDING);
        hostCacheStage.setStageOrder(stageOrder);
        hostCacheStage.setCluster(cluster);

        hostCacheStage.setCallbackClassName(this.getClass().getName());
        hostCacheStage = stageRepository.save(hostCacheStage);

        for (String hostname : hostnames) {
            Task task = new Task();
            task.setJob(job);
            task.setStage(hostCacheStage);
            task.setCluster(cluster);
            task.setStackName(cluster.getStack().getStackName());
            task.setStackVersion(cluster.getStack().getStackVersion());
            task.setHostname(hostname);
            task.setServiceName("cluster");
            task.setServiceUser("root");
            task.setServiceGroup("root");
            task.setComponentName("bigtop-manager-agent");
            task.setCommand(Command.CUSTOM_COMMAND);
            task.setCustomCommand("cache_host");
            task.setState(JobState.PENDING);

            RequestMessage requestMessage = getMessage(
                    hostname,
                    settingsMap,
                    clusterInfo,
                    serviceConfigMap,
                    hostMap,
                    repoList,
                    userMap,
                    componentInfoMap);
            log.info("[HostCacheJobFactory-requestMessage]: {}", requestMessage);
            task.setContent(JsonUtils.writeAsString(requestMessage));

            task.setMessageId(requestMessage.getMessageId());
            taskRepository.save(task);
        }
    }

    private ClusterInfo clusterInfo;

    private Map<String, ComponentInfo> componentInfoMap;

    private Map<String, Map<String, Object>> serviceConfigMap;

    private Map<String, Set<String>> hostMap;

    private List<RepoInfo> repoList;

    private Map<String, Set<String>> userMap;

    private Map<String, Object> settingsMap;

    private void createCache(Cluster cluster) {
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
        clusterInfo = new ClusterInfo();
        clusterInfo.setClusterName(clusterName);
        clusterInfo.setStackName(stackName);
        clusterInfo.setStackVersion(stackVersion);
        clusterInfo.setUserGroup(cluster.getUserGroup());
        clusterInfo.setRepoTemplate(cluster.getRepoTemplate());
        clusterInfo.setRoot(cluster.getRoot());

        try {
            Set<String> packages = Sets.newHashSet(cluster.getPackages().split(","));
            clusterInfo.setPackages(packages);
        } catch (Exception e) {
            log.warn("no packages");
        }

        //Wrapper serviceConfigMap for HostCacheMessage
        serviceConfigMap = new HashMap<>();
        serviceConfigMappingList.forEach(scm -> {
            ServiceConfig sc = scm.getServiceConfig();
            List<PropertyDTO> properties = JsonUtils.readFromString(sc.getConfigData(), new TypeReference<>() {
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
        hostMap = new HashMap<>();
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
        repoList = new ArrayList<>();
        repos.forEach(repo -> {
            RepoInfo repoInfo = RepoMapper.INSTANCE.fromEntity2Message(repo);
            repoList.add(repoInfo);
        });

        //Wrapper userMap for HostCacheMessage
        userMap = new HashMap<>();
        services.forEach(x -> userMap.put(x.getServiceUser(), Set.of(x.getServiceGroup())));

        //Wrapper settings for HostCacheMessage
        settingsMap = new HashMap<>();
        settings.forEach(x -> settingsMap.put(x.getTypeName(), x.getConfigData()));

        //Wrapper componentInfoList for HostCacheMessage
        componentInfoMap = new HashMap<>();
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
    }

    public RequestMessage getMessage(String hostname,
                                     Map<String, Object> settingsMap,
                                     ClusterInfo clusterInfo,
                                     Map<String, Map<String, Object>> serviceConfigMap,
                                     Map<String, Set<String>> hostMap,
                                     List<RepoInfo> repoList,
                                     Map<String, Set<String>> userMap,
                                     Map<String, ComponentInfo> componentInfoMap) {
        HostCachePayload hostCachePayload = getMessagePayload(
                hostname,
                settingsMap,
                clusterInfo,
                serviceConfigMap,
                hostMap,
                repoList,
                userMap,
                componentInfoMap);
        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setMessageType(MessageType.HOST_CACHE);
        requestMessage.setHostname(hostname);

        requestMessage.setMessagePayload(JsonUtils.writeAsString(hostCachePayload));
        return requestMessage;
    }

    private HostCachePayload getMessagePayload(
            String hostname,
            Map<String, Object> settingsMap,
            ClusterInfo clusterInfo,
            Map<String, Map<String, Object>> serviceConfigMap,
            Map<String, Set<String>> hostMap,
            List<RepoInfo> repoList,
            Map<String, Set<String>> userMap,
            Map<String, ComponentInfo> componentInfoMap) {
        HostCachePayload hostCacheMessage = new HostCachePayload();
        hostCacheMessage.setHostname(hostname);

        hostCacheMessage.setClusterInfo(clusterInfo);
        hostCacheMessage.setConfigurations(serviceConfigMap);
        hostCacheMessage.setClusterHostInfo(hostMap);
        hostCacheMessage.setRepoInfo(repoList);
        hostCacheMessage.setSettings(settingsMap);
        hostCacheMessage.setUserInfo(userMap);
        hostCacheMessage.setComponentInfo(componentInfoMap);
        return hostCacheMessage;
    }

    @Override
    public String generatePayload(Task task) {
        Cluster cluster = task.getCluster();
        createCache(cluster);
        RequestMessage requestMessage = getMessage(
                task.getHostname(),
                settingsMap,
                clusterInfo,
                serviceConfigMap,
                hostMap,
                repoList,
                userMap,
                componentInfoMap);
        log.info("[generatePayload]-[HostCacheJobFactory-requestMessage]: {}", requestMessage);
        return JsonUtils.writeAsString(requestMessage);
    }
}
