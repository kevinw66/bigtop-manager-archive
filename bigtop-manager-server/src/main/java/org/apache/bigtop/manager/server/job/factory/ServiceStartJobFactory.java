package org.apache.bigtop.manager.server.job.factory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.ServiceComponentStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
public class ServiceStartJobFactory implements JobFactory, StageCallback {

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private ServiceComponentStageHelper serviceComponentStageHelper;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.SERVICE, Command.START);
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

    @Override
    public void afterStage(Stage stage) {
        Long clusterId = stage.getCluster().getId();
        String componentName = stage.getComponentName();

        if (stage.getState() == JobState.SUCCESSFUL) {
            List<HostComponent> hostComponents = hostComponentRepository.findAllByComponentClusterIdAndComponentComponentName(clusterId, componentName);
            Service service = hostComponents.get(0).getComponent().getService();

            hostComponents.forEach(hostComponent -> hostComponent.setState(MaintainState.STARTED));
            hostComponentRepository.saveAll(hostComponents);

            if (hostComponents.stream().allMatch(x -> x.getState() == MaintainState.STARTED)) {
                service.setState(MaintainState.STARTED);
            }
            serviceRepository.save(service);
        }
    }

}
