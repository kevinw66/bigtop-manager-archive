package org.apache.bigtop.manager.server.ws;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.HostCacheMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.BasicInfo;
import org.apache.bigtop.manager.common.message.type.pojo.ClusterInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@org.springframework.stereotype.Component
public class HostCacheHandler implements Callback {

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
    private ServerWebSocketHandler serverWebSocketHandler;

    @Subscribe
    public void cache(Long clusterId) {

        Cluster cluster = clusterRepository.findById(clusterId).orElse(new Cluster());

        String clusterName = cluster.getClusterName();
        Long stackId = cluster.getStack().getId();
        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();

        List<Service> services = serviceRepository.findAllByClusterId(clusterId);
        List<ServiceConfig> serviceConfigs = serviceConfigRepository.findAllByClusterId(clusterId);
        List<HostComponent> hostComponents = hostComponentRepository.findAllByComponentClusterId(clusterId);
        List<Repo> repos = repoRepository.findAllByStackId(stackId);
        Setting setting = settingRepository.findFirstByOrderByVersionDesc().orElse(new Setting());
        List<Host> hostList = hostRepository.findAllByClusterId(clusterId);


        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterName(clusterName);
        clusterInfo.setStackName(stackName);
        clusterInfo.setStackVersion(stackVersion);
        clusterInfo.setUserGroup(cluster.getUserGroup());
        clusterInfo.setRepoTemplate(cluster.getRepoTemplate());
        clusterInfo.setRoot(cluster.getRoot());

        Set<String> packages = JsonUtils.string2Json(cluster.getPackages(), new TypeReference<>() {
        });
        clusterInfo.setPackages(packages);

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

        List<RepoInfo> repoList = new ArrayList<>();
        repos.forEach(repo -> {
            RepoInfo repoInfo = RepoMapper.INSTANCE.Entity2Message(repo);
            repoList.add(repoInfo);
        });

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

        BasicInfo basicInfo = new BasicInfo();
        basicInfo.setJavaHome(setting.getJavaHome());
        basicInfo.setJavaVersion(setting.getJavaVersion());
        basicInfo.setJdbcDriver(setting.getJdbcDriver());
        basicInfo.setJdbcDriverHome(setting.getJdbcDriverHome());

        //Wrapper HostCacheMessage for websocket
        HostCacheMessage hostCacheMessage = new HostCacheMessage();

        hostCacheMessage.setStack(stackName);
        hostCacheMessage.setVersion(stackVersion);
        hostCacheMessage.setCacheDir(cluster.getCacheDir());

        hostCacheMessage.setClusterInfo(clusterInfo);
        hostCacheMessage.setConfigurations(serviceConfigMap);
        hostCacheMessage.setClusterHostInfo(hostMap);
        hostCacheMessage.setRepoInfo(repoList);
        hostCacheMessage.setBasicInfo(basicInfo);
        hostCacheMessage.setUserInfo(userMap);

        for (Host host : hostList) {
            String hostname = host.getHostname();
            hostCacheMessage.setHostname(hostname);
            hostCacheMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));

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

    private CountDownLatch countDownLatch;

    @Override
    public void call(ResultMessage resultMessage) {
        countDownLatch.countDown();
        if (resultMessage.getCode() == 0) {
            log.info("host cache success");
        } else {
            log.error("host cache failed");
        }
    }
}
