package org.apache.bigtop.manager.server.job.factory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.HostCacheStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceConfigDTO;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.service.ConfigService;
import org.springframework.beans.BeanUtils;

@Slf4j
@org.springframework.stereotype.Component
public class ServiceConfigureJobFactory implements JobFactory, StageCallback {

    @Resource
    private HostCacheStageHelper hostCacheStageHelper;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private ConfigService configService;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.SERVICE, Command.CONFIGURE);
    }

    @Override
    public Job createJob(JobContext context) {
        Long clusterId = context.getCommandDTO().getClusterId();
        Job job = new Job();

        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        // Create job
        job.setName("Configure Services");
        job.setState(JobState.PENDING);
        job.setCluster(cluster);
        job = jobRepository.save(job);

        // Create stages
        hostCacheStageHelper.createStage(job, cluster, 1, this.getClass().getName(), JsonUtils.writeAsString(context.getCommandDTO()));

        return job;
    }

    @Override
    public void beforeStage(Stage stage) {
        CommandDTO commandDTO = JsonUtils.readFromString(stage.getPayload(), CommandDTO.class);

        Cluster cluster = clusterRepository.getReferenceById(commandDTO.getClusterId());
        for (ServiceCommandDTO serviceCommand : commandDTO.getServiceCommands()) {
            ServiceConfigDTO serviceConfigDTO = new ServiceConfigDTO();
            BeanUtils.copyProperties(serviceCommand, serviceConfigDTO);
            configService.updateConfig(cluster, serviceConfigDTO);
        }
    }
}
