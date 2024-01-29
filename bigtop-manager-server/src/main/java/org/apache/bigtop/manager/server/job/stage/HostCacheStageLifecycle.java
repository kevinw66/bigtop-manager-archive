package org.apache.bigtop.manager.server.job.stage;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.HostCachePayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.dto.PropertyDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.utils.StackConfigUtils;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.bigtop.manager.common.constants.Constants.ALL_HOST_KEY;
import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HostCacheStageLifecycle extends AbstractStageLifecycle {

    @Resource
    private ClusterRepository clusterRepository;

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

    private ClusterInfo clusterInfo;

    private Map<String, ComponentInfo> componentInfoMap;

    private Map<String, Map<String, Object>> serviceConfigMap;

    private Map<String, Set<String>> hostMap;

    private List<RepoInfo> repoList;

    private Map<String, Set<String>> userMap;

    private Map<String, Object> settingsMap;

    @Override
    public StageType getStageType() {
        return StageType.HOST_CACHE;
    }

    @Override
    public Stage createStage() {
        if (context.getClusterId() == null) {
            genEmptyCaches();
        } else {
            genCaches();
        }

        Stage hostCacheStage = new Stage();
        hostCacheStage.setName(CACHE_STAGE_NAME);

        List<Task> tasks = new ArrayList<>();
        for (String hostname : context.getHostnames()) {
            Task task = new Task();
            task.setName("Cache host for " + hostname);
            task.setStackName(context.getStackName());
            task.setStackVersion(context.getStackVersion());
            task.setHostname(hostname);
            task.setServiceName("cluster");
            task.setServiceUser("root");
            task.setServiceGroup("root");
            task.setComponentName("bigtop-manager-agent");
            task.setCommand(Command.CUSTOM_COMMAND);
            task.setCustomCommand("cache_host");

            RequestMessage requestMessage = getMessage(hostname);

            log.info("[HostCacheJobFactory-requestMessage]: {}", requestMessage);
            task.setContent(JsonUtils.writeAsString(requestMessage));
            task.setMessageId(requestMessage.getMessageId());
            tasks.add(task);
        }

        hostCacheStage.setTasks(tasks);
        return hostCacheStage;
    }

    private void genCaches() {
        Cluster cluster = clusterRepository.getReferenceById(context.getClusterId());

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

        // Wrapper clusterInfo for HostCacheMessage
        clusterInfo = new ClusterInfo();
        clusterInfo.setClusterName(clusterName);
        clusterInfo.setStackName(stackName);
        clusterInfo.setStackVersion(stackVersion);
        clusterInfo.setUserGroup(cluster.getUserGroup());
        clusterInfo.setRepoTemplate(cluster.getRepoTemplate());
        clusterInfo.setRoot(cluster.getRoot());
        clusterInfo.setPackages(List.of(cluster.getPackages().split(",")));

        // Wrapper serviceConfigMap for HostCacheMessage
        serviceConfigMap = new HashMap<>();
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

        // Wrapper hostMap for HostCacheMessage
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

        // Wrapper repoList for HostCacheMessage
        repoList = new ArrayList<>();
        repos.forEach(repo -> {
            RepoInfo repoInfo = RepoMapper.INSTANCE.fromEntity2Message(repo);
            repoList.add(repoInfo);
        });

        // Wrapper userMap for HostCacheMessage
        userMap = new HashMap<>();
        services.forEach(x -> userMap.put(x.getServiceUser(), Set.of(x.getServiceGroup())));

        // Wrapper settings for HostCacheMessage
        settingsMap = new HashMap<>();
        settings.forEach(x -> settingsMap.put(x.getTypeName(), x.getConfigData()));

        // Wrapper componentInfoList for HostCacheMessage
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

    private void genEmptyCaches() {
        componentInfoMap = new HashMap<>();
        serviceConfigMap = new HashMap<>();
        hostMap = new HashMap<>();
        userMap = new HashMap<>();
        settingsMap = new HashMap<>();

        String fullStackName = StackUtils.fullStackName(context.getStackName(), context.getStackVersion());
        ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = StackUtils.getStackKeyMap().get(fullStackName);
        StackDTO stackDTO = immutablePair.getLeft();
        List<ServiceDTO> serviceDTOList = immutablePair.getRight();

        repoList = RepoMapper.INSTANCE.fromDTO2Message(context.getRepoInfoList());
        clusterInfo = new ClusterInfo();
        clusterInfo.setClusterName(context.getClusterName());
        clusterInfo.setStackName(context.getStackName());
        clusterInfo.setStackVersion(context.getStackVersion());
        clusterInfo.setUserGroup(stackDTO.getUserGroup());
        clusterInfo.setRepoTemplate(stackDTO.getRepoTemplate());
        clusterInfo.setRoot(stackDTO.getRoot());

        List<String> hostnames = context.getHostnames();
        hostMap.put(Constants.ALL_HOST_KEY, new HashSet<>(hostnames));

        for (ServiceDTO serviceDTO : serviceDTOList) {
            userMap.put(serviceDTO.getServiceUser(), Set.of(serviceDTO.getServiceGroup()));
        }
    }

    private RequestMessage getMessage(String hostname) {
        HostCachePayload messagePayload = new HostCachePayload();
        messagePayload.setHostname(hostname);
        messagePayload.setClusterInfo(clusterInfo);
        messagePayload.setConfigurations(serviceConfigMap);
        messagePayload.setClusterHostInfo(hostMap);
        messagePayload.setRepoInfo(repoList);
        messagePayload.setSettings(settingsMap);
        messagePayload.setUserInfo(userMap);
        messagePayload.setComponentInfo(componentInfoMap);

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMessageType(MessageType.HOST_CACHE);
        requestMessage.setHostname(hostname);
        requestMessage.setMessagePayload(JsonUtils.writeAsString(messagePayload));

        return requestMessage;
    }
}
