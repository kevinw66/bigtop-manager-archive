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
import org.apache.bigtop.manager.server.orm.repository.StageRepository;
import org.apache.bigtop.manager.server.orm.repository.TaskRepository;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
public class HostCheckStageHelper {

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    public void createStage(Job job, Cluster cluster, List<String> hostnames, int stageOrder) {
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
            task.setName("Check host for " + hostname);
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

            RequestMessage requestMessage = createMessage(hostname);
            task.setContent(JsonUtils.writeAsString(requestMessage));

            task.setMessageId(requestMessage.getMessageId());
            taskRepository.save(task);
        }
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
