package org.apache.bigtop.manager.server.command.stage.runner.config;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;
import org.apache.bigtop.manager.common.message.entity.payload.CacheMessagePayload;
import org.apache.bigtop.manager.common.message.entity.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.entity.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.entity.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.dao.entity.*;
import org.apache.bigtop.manager.dao.repository.*;
import org.apache.bigtop.manager.server.command.stage.factory.StageContext;
import org.apache.bigtop.manager.server.command.stage.factory.StageType;
import org.apache.bigtop.manager.server.command.stage.runner.AbstractStageRunner;
import org.apache.bigtop.manager.server.model.dto.PropertyDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.utils.StackConfigUtils;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.bigtop.manager.common.constants.Constants.ALL_HOST_KEY;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CacheDistributeStageRunner extends AbstractStageRunner {

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
        return StageType.CACHE_DISTRIBUTE;
    }

    @Override
    public void beforeRunTask(Task task) {
        super.beforeRunTask(task);

        // Generate task content before execute
        updateTask(task);
    }

    private void updateTask(Task task) {
        StageContext context = JsonUtils.readFromString(stage.getContext(), StageContext.class);
        if (context.getClusterId() == null) {
            genEmptyCaches(context);
        } else {
            genCaches(context);
        }

        CommandRequestMessage commandRequestMessage = getMessage(task.getHostname());
        task.setContent(JsonUtils.writeAsString(commandRequestMessage));
        task.setMessageId(commandRequestMessage.getMessageId());

        taskRepository.save(task);
    }

    private void genCaches(StageContext context) {
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

        clusterInfo = new ClusterInfo();
        clusterInfo.setClusterName(clusterName);
        clusterInfo.setStackName(stackName);
        clusterInfo.setStackVersion(stackVersion);
        clusterInfo.setUserGroup(cluster.getUserGroup());
        clusterInfo.setRepoTemplate(cluster.getRepoTemplate());
        clusterInfo.setRoot(cluster.getRoot());
        clusterInfo.setPackages(List.of(cluster.getPackages().split(",")));

        serviceConfigMap = new HashMap<>();
        serviceConfigMappingList.forEach(scm -> {
            ServiceConfig sc = scm.getServiceConfig();
            List<PropertyDTO> properties = JsonUtils.readFromString(sc.getPropertiesJson(), new TypeReference<>() {});
            String configMapStr = JsonUtils.writeAsString(StackConfigUtils.extractConfigMap(properties));

            if (serviceConfigMap.containsKey(sc.getService().getServiceName())) {
                serviceConfigMap.get(sc.getService().getServiceName()).put(sc.getTypeName(), configMapStr);
            } else {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put(sc.getTypeName(), configMapStr);
                serviceConfigMap.put(sc.getService().getServiceName(), hashMap);
            }
        });

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

        repoList = new ArrayList<>();
        repos.forEach(repo -> {
            RepoInfo repoInfo = RepoMapper.INSTANCE.fromEntity2Message(repo);
            repoList.add(repoInfo);
        });

        userMap = new HashMap<>();
        services.forEach(x -> userMap.put(x.getServiceUser(), Set.of(x.getServiceGroup())));

        settingsMap = new HashMap<>();
        settings.forEach(x -> settingsMap.put(x.getTypeName(), x.getConfigData()));

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

    private void genEmptyCaches(StageContext context) {
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

    private CommandRequestMessage getMessage(String hostname) {
        CacheMessagePayload messagePayload = new CacheMessagePayload();
        messagePayload.setHostname(hostname);
        messagePayload.setClusterInfo(clusterInfo);
        messagePayload.setConfigurations(serviceConfigMap);
        messagePayload.setClusterHostInfo(hostMap);
        messagePayload.setRepoInfo(repoList);
        messagePayload.setSettings(settingsMap);
        messagePayload.setUserInfo(userMap);
        messagePayload.setComponentInfo(componentInfoMap);

        CommandRequestMessage commandRequestMessage = new CommandRequestMessage();
        commandRequestMessage.setMessageType(MessageType.CACHE_DISTRIBUTE);
        commandRequestMessage.setHostname(hostname);
        commandRequestMessage.setMessagePayload(JsonUtils.writeAsString(messagePayload));

        return commandRequestMessage;
    }
}
