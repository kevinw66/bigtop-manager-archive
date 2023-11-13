package org.apache.bigtop.manager.server.listener.factory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.message.type.HostCheckPayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Component
public class HostAddJobFactory implements JobFactory {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private HostCacheJobFactory hostCacheJobFactory;

    public Job createJob(Long clusterId, List<String> hostnames) {
        Job job = new Job();
        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        saveHost(cluster, hostnames);

        // Create job
        job.setContext("Add Hosts");
        job.setState(JobState.PENDING);
        job.setCluster(cluster);
        job = jobRepository.save(job);

        // Create stages
        createHostCheckStage(job, cluster, hostnames, 1);

        hostCacheJobFactory.createStage(job, cluster, 2);

        return job;
    }

    public void createHostCheckStage(Job job, Cluster cluster, List<String> hostnames, int stageOrder) {
        // Create stages
        Stage hostCheckStage = new Stage();
        hostCheckStage.setJob(job);
        hostCheckStage.setName("Check Hosts");
        hostCheckStage.setState(JobState.PENDING);
        hostCheckStage.setStageOrder(stageOrder);
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
            task.setCustomCommand("check_host");
            task.setState(JobState.PENDING);

            RequestMessage requestMessage = getMessage(hostname);
            task.setContent(JsonUtils.writeAsString(requestMessage));

            task.setMessageId(requestMessage.getMessageId());
            taskRepository.save(task);
        }
    }

    private RequestMessage getMessage(String hostname) {
        HostCheckPayload hostCheckPayload = getMessagePayload(hostname);
        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setMessageType(MessageType.HOST_CHECK);
        requestMessage.setHostname(hostname);

        requestMessage.setMessagePayload(JsonUtils.writeAsString(hostCheckPayload));
        return requestMessage;
    }

    private HostCheckPayload getMessagePayload(String hostname) {
        HostCheckPayload hostCheckMessage = new HostCheckPayload();
        hostCheckMessage.setHostCheckTypes(HostCheckType.values());
        hostCheckMessage.setHostname(hostname);
        return hostCheckMessage;
    }

    public void saveHost(Cluster cluster, List<String> hostnames) {
        List<Host> hostnameIn = hostRepository.findAllByHostnameIn(hostnames);
        List<Host> hosts = new ArrayList<>();

        Map<String, Host> hostInMap = hostnameIn.stream().collect(Collectors.toMap(Host::getHostname, host -> host));

        for (String hostname : hostnames) {
            Host host = new Host();
            host.setHostname(hostname);
            host.setCluster(cluster);
            host.setState(MaintainState.UNINSTALLED);

            if (hostInMap.containsKey(hostname)) {
                host.setId(hostInMap.get(hostname).getId());
            }

            hosts.add(host);
        }
        hostRepository.saveAll(hosts);
    }

}
