package org.apache.bigtop.manager.server.listener.operator;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.StageRepository;
import org.apache.bigtop.manager.server.orm.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
public class HostAddJobFactory {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;


    public Job createJob(Long clusterId, List<String> hostnames) {
        Job job = new Job();

        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        // Create job
        job.setContext("Add Hosts");
        job.setState(JobState.PENDING);
        job.setCluster(cluster);
        job = jobRepository.save(job);

        // Create stages
        Stage hostCheckStage = new Stage();
        hostCheckStage.setJob(job);
        hostCheckStage.setName("Check Hosts");
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
            task.setCustomCommand("check_host");
            task.setState(JobState.PENDING);
            taskRepository.save(task);
        }

        return job;
    }

}
