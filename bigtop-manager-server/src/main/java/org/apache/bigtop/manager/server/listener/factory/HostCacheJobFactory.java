package org.apache.bigtop.manager.server.listener.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.HostCacheMessage;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.bigtop.manager.common.constants.Constants.ALL_HOST_KEY;

@Slf4j
@org.springframework.stereotype.Component
public class HostCacheJobFactory implements JobFactory {

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private ServiceConfigRepository serviceConfigRepository;

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

        createCache(cluster);

        // Create job
        job.setContext("Cache Hosts");
        job.setState(JobState.PENDING);
        job.setCluster(cluster);
        job = jobRepository.save(job);

        // Create stages
        createStage(job, cluster);

        return job;
    }

    public void createStage(Job job, Cluster cluster) {
        List<Host> hostList = hostRepository.findAllByClusterId(cluster.getId());
        List<String> hostnames = hostList.stream().map(Host::getHostname).toList();

        Stage hostCheckStage = new Stage();
        hostCheckStage.setJob(job);
        hostCheckStage.setName("Cache Host");
        hostCheckStage.setState(JobState.PENDING);
        hostCheckStage.setStageOrder(1);
        hostCheckStage.setCluster(cluster);
        hostCheckStage = stageRepository.save(hostCheckStage);

        for (String hostname : hostnames) {
            Task task = new Task();
            task.setJob(job);
            task.setStage(hostCheckStage);
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

            HostCacheMessage hostCacheMessage = getTaskContent(
                    hostname,
                    settingsMap,
                    clusterInfo,
                    serviceConfigMap,
                    hostMap,
                    repoList,
                    userMap,
                    componentInfoMap);
            task.setContent(JsonUtils.writeAsString(hostCacheMessage));

            task.setMessageId(hostCacheMessage.getMessageId());
            taskRepository.save(task);
        }
    }

    private final ClusterInfo clusterInfo = new ClusterInfo();

    private final Map<String, ComponentInfo> componentInfoMap = new HashMap<>();

    private final Map<String, Map<String, Object>> serviceConfigMap = new HashMap<>();

    private final Map<String, Set<String>> hostMap = new HashMap<>();

    private final List<RepoInfo> repoList = new ArrayList<>();

    private final Map<String, Set<String>> userMap = new HashMap<>();

    private final Map<String, Object> settingsMap = new HashMap<>();

    private void createCache(Cluster cluster) {
        Long clusterId = cluster.getId();

        String clusterName = cluster.getClusterName();
        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();

        List<Service> services = serviceRepository.findAllByClusterId(clusterId);
        List<ServiceConfig> serviceConfigs = serviceConfigRepository.findAllByClusterId(clusterId);
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

        try {
            Set<String> packages = JsonUtils.readFromString(cluster.getPackages(), new TypeReference<>() {
            });
            clusterInfo.setPackages(packages);
        } catch (Exception e) {
            log.warn("no packages");
        }

        //Wrapper serviceConfigMap for HostCacheMessage
        serviceConfigs.forEach(x -> {
            if (serviceConfigMap.containsKey(x.getService().getServiceName())) {
                serviceConfigMap.get(x.getService().getServiceName()).put(x.getTypeName(), x.getConfigData());
            } else {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put(x.getTypeName(), x.getConfigData());
                serviceConfigMap.put(x.getService().getServiceName(), hashMap);
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
            RepoInfo repoInfo = RepoMapper.INSTANCE.Entity2Message(repo);
            repoList.add(repoInfo);
        });

        //Wrapper userMap for HostCacheMessage
        services.forEach(x -> {
            if (userMap.containsKey(x.getServiceUser())) {
                userMap.get(x.getServiceUser()).add(x.getServiceGroup());
            } else {
                Set<String> set = new HashSet<>();
                set.add(x.getServiceGroup());
                userMap.put(x.getServiceUser(), set);
            }
        });

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
    }

    private HostCacheMessage getTaskContent(
            String hostname,
            Map<String, Object> settingsMap,
            ClusterInfo clusterInfo,
            Map<String, Map<String, Object>> serviceConfigMap,
            Map<String, Set<String>> hostMap,
            List<RepoInfo> repoList,
            Map<String, Set<String>> userMap,
            Map<String, ComponentInfo> componentInfoList) {
        HostCacheMessage hostCacheMessage = new HostCacheMessage();
        hostCacheMessage.setHostname(hostname);

        hostCacheMessage.setClusterInfo(clusterInfo);
        hostCacheMessage.setConfigurations(serviceConfigMap);
        hostCacheMessage.setClusterHostInfo(hostMap);
        hostCacheMessage.setRepoInfo(repoList);
        hostCacheMessage.setSettings(settingsMap);
        hostCacheMessage.setUserInfo(userMap);
        hostCacheMessage.setComponentInfo(componentInfoList);
        return hostCacheMessage;
    }

}
