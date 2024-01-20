package org.apache.bigtop.manager.server.job.factory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.HostCacheStageHelper;
import org.apache.bigtop.manager.server.job.helper.ServiceComponentStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.service.HostComponentService;

import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;

@Slf4j
@org.springframework.stereotype.Component
public class ComponentInstallJobFactory implements JobFactory, StageCallback {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private HostCacheStageHelper hostCacheStageHelper;

    @Resource
    private HostComponentService hostComponentService;

    @Resource
    private ServiceComponentStageHelper serviceComponentStageHelper;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.COMPONENT, Command.INSTALL);
    }

    /**
     * create job and persist it to database
     *
     * @param context command DTO
     * @return task flow queue
     */
    @Override
    public Job createJob(JobContext context) {
        CommandDTO commandDTO = context.getCommandDTO();
        Long clusterId = commandDTO.getClusterId();
        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        Job job = new Job();
        job.setState(JobState.PENDING);
        job.setName(commandDTO.getContext());
        job.setCluster(cluster);
        job = jobRepository.save(job);
        log.info("CommandOperator-job: {}", job);

        int stageOrder = 0;
        // command stage
        stageOrder = serviceComponentStageHelper.createStage(job, commandDTO, stageOrder, this.getClass().getName());
        // cache stage
        stageOrder += 1;
        hostCacheStageHelper.createStage(job, cluster, stageOrder, this.getClass().getName(), JsonUtils.writeAsString(commandDTO));

        return job;
    }

    @Override
    public void beforeStage(Stage stage) {
        CommandDTO commandDTO = JsonUtils.readFromString(stage.getPayload(), CommandDTO.class);
        if (stage.getName().equals(CACHE_STAGE_NAME) && commandDTO.getCommand() == Command.INSTALL && commandDTO.getCommandLevel() == CommandLevel.COMPONENT) {
            hostComponentService.saveByCommand(commandDTO);
        }
    }

    @Override
    public String generatePayload(Task task) {
        Cluster cluster = task.getCluster();
        hostCacheStageHelper.createCache(cluster);
        RequestMessage requestMessage = hostCacheStageHelper.getMessage(task.getHostname());
        log.info("[generatePayload]-[HostCacheJobFactory-requestMessage]: {}", requestMessage);
        return JsonUtils.writeAsString(requestMessage);
    }

}
