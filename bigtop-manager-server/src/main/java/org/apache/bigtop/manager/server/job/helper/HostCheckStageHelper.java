package org.apache.bigtop.manager.server.job.helper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.HostCheckPayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.StageRepository;
import org.apache.bigtop.manager.server.orm.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
public class HostCheckStageHelper {

    @Resource
    private ClusterRepository clusterRepository;

    public Stage createStage(Long clusterId, List<String> hostnames, String callbackClassName) {
        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();
        return createStage(stackName, stackVersion, hostnames, callbackClassName);
    }

    public Stage createStage(String stackName, String stackVersion, List<String> hostnames, String callbackClassName) {
        // Create stages
        Stage hostCheckStage = new Stage();
        hostCheckStage.setName("Check Hosts");
        hostCheckStage.setCallbackClassName(callbackClassName);

        List<Task> tasks = new ArrayList<>();
        for (String hostname : hostnames) {
            Task task = new Task();
            task.setName("Check host for " + hostname);
            task.setStackName(stackName);
            task.setStackVersion(stackVersion);
            task.setHostname(hostname);
            task.setServiceName("cluster");
            task.setServiceUser("root");
            task.setServiceGroup("root");
            task.setComponentName("bigtop-manager-agent");
            task.setCommand(Command.CUSTOM_COMMAND);
            task.setCustomCommand("check_host");

            RequestMessage requestMessage = createMessage(hostname);
            task.setContent(JsonUtils.writeAsString(requestMessage));
            task.setMessageId(requestMessage.getMessageId());

            tasks.add(task);
        }

        hostCheckStage.setTasks(tasks);
        return hostCheckStage;
    }

    public RequestMessage createMessage(String hostname) {
        HostCheckPayload hostCheckPayload = createMessagePayload(hostname);
        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setMessageType(MessageType.HOST_CHECK);
        requestMessage.setHostname(hostname);

        requestMessage.setMessagePayload(JsonUtils.writeAsString(hostCheckPayload));
        return requestMessage;
    }

    public HostCheckPayload createMessagePayload(String hostname) {
        HostCheckPayload hostCheckMessage = new HostCheckPayload();
        hostCheckMessage.setHostCheckTypes(HostCheckType.values());
        hostCheckMessage.setHostname(hostname);
        return hostCheckMessage;
    }
}
