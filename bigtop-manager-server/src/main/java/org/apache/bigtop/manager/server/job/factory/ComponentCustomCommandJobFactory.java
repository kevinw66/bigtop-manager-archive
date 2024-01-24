package org.apache.bigtop.manager.server.job.factory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.ServiceComponentStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;

@Slf4j
@org.springframework.stereotype.Component
public class ComponentCustomCommandJobFactory implements JobFactory, StageCallback {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private ServiceComponentStageHelper serviceComponentStageHelper;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.COMPONENT, Command.CUSTOM_COMMAND);
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

        serviceComponentStageHelper.createStage(job, commandDTO, 0, this.getClass().getName());

        return job;
    }

}
