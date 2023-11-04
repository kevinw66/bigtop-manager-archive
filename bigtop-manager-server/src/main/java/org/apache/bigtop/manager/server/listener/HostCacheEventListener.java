package org.apache.bigtop.manager.server.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.HostCacheMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ComponentInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.event.HostCacheEvent;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.ws.Callback;
import org.apache.bigtop.manager.server.ws.ServerWebSocketHandler;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.apache.bigtop.manager.common.constants.Constants.ALL_HOST_KEY;

@Slf4j
@org.springframework.stereotype.Component
public class HostCacheEventListener implements Callback {

    @Resource
    private AsyncEventBus asyncEventBus;

    @PostConstruct
    public void init() {
        asyncEventBus.register(this);
    }

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
    private ServerWebSocketHandler serverWebSocketHandler;

    @Subscribe
    public void handleHostCache(HostCacheEvent event) {
        log.info("listen HostCacheEvent: {}", event);
        Long clusterId = event.getClusterId();

        Cluster cluster = clusterRepository.findById(clusterId).orElse(new Cluster());

        String clusterName = cluster.getClusterName();
        Long stackId = cluster.getStack().getId();
        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();

        List<Service> services = serviceRepository.findAllByClusterId(clusterId);
        List<ServiceConfig> serviceConfigs = serviceConfigRepository.findAllByClusterId(clusterId);
        List<HostComponent> hostComponents = hostComponentRepository.findAllByComponentClusterId(clusterId);
        List<Repo> repos = repoRepository.findAllByCluster(cluster);
        Iterable<Setting> settings = settingRepository.findAll();
        List<Host> hostList = hostRepository.findAllByClusterId(clusterId);

        //Wrapper clusterInfo for HostCacheMessage
        ClusterInfo clusterInfo = new ClusterInfo();
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
        Map<String, Map<String, Object>> serviceConfigMap = new HashMap<>();
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
        Map<String, Set<String>> hostMap = new HashMap<>();
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
        List<RepoInfo> repoList = new ArrayList<>();
        repos.forEach(repo -> {
            RepoInfo repoInfo = RepoMapper.INSTANCE.Entity2Message(repo);
            repoList.add(repoInfo);
        });

        //Wrapper userMap for HostCacheMessage
        Map<String, Set<String>> userMap = new HashMap<>();
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
        Map<String, Object> settingsMap = new HashMap<>();
        settings.forEach(x -> {
            settingsMap.put(x.getTypeName(), x.getConfigData());
        });


        Map<String, ComponentInfo> componentInfoMap = new HashMap<>();
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


        for (Host host : hostList) {
            String hostname = host.getHostname();
            //Wrapper HostCacheMessage for websocket
            HostCacheMessage hostCacheMessage = convertMessage(hostname,
                    settingsMap,
                    clusterInfo,
                    serviceConfigMap,
                    hostMap,
                    repoList,
                    userMap,
                    componentInfoMap);

            log.info("hostCacheMessage: {}", hostCacheMessage);
            serverWebSocketHandler.sendMessage(hostname, hostCacheMessage, this);
        }

        countDownLatch = new CountDownLatch(hostList.size());
        try {
            boolean timeoutFlag = countDownLatch.await(30, TimeUnit.SECONDS);
            if (!timeoutFlag) {
                log.error("execute task timeout");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private HostCacheMessage convertMessage(String hostname,
                                            Map<String, Object> settingsMap,
                                            ClusterInfo clusterInfo,
                                            Map<String, Map<String, Object>> serviceConfigMap,
                                            Map<String, Set<String>> hostMap,
                                            List<RepoInfo> repoList,
                                            Map<String, Set<String>> userMap,
                                            Map<String, ComponentInfo> componentInfoList) {
        HostCacheMessage hostCacheMessage = new HostCacheMessage();

        hostCacheMessage.setClusterInfo(clusterInfo);
        hostCacheMessage.setConfigurations(serviceConfigMap);
        hostCacheMessage.setClusterHostInfo(hostMap);
        hostCacheMessage.setRepoInfo(repoList);
        hostCacheMessage.setSettings(settingsMap);
        hostCacheMessage.setUserInfo(userMap);
        hostCacheMessage.setHostname(hostname);
        hostCacheMessage.setComponentInfo(componentInfoList);
        return hostCacheMessage;
    }

    private CountDownLatch countDownLatch;

    @Override
    public void call(ResultMessage resultMessage) {
        countDownLatch.countDown();
        if (resultMessage.getCode() == 0) {
            log.info("Host cache success, {}", resultMessage);
        } else {
            log.error("Host cache failed, {}", resultMessage);
        }
    }
}
