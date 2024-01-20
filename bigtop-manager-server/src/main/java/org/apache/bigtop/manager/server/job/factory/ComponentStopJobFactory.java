package org.apache.bigtop.manager.server.job.factory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.ServiceComponentStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.ComponentCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
public class ComponentStopJobFactory implements JobFactory, StageCallback {

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
        return new CommandIdentifier(CommandLevel.COMPONENT, Command.STOP);
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
        serviceComponentStageHelper.createStage(job, commandDTO, stageOrder, this.getClass().getName());

        return job;
    }

    @Override
    public void afterStage(Stage stage) {
        Long clusterId = stage.getCluster().getId();
        String componentName = stage.getComponentName();
        CommandDTO commandDTO = JsonUtils.readFromString(stage.getPayload(), CommandDTO.class);
        List<ComponentCommandDTO> componentCommands = commandDTO.getComponentCommands();
        List<String> hostnames = componentCommands
                .stream()
                .filter(x -> x.getComponentName().equals(componentName))
                .flatMap(x -> x.getHostnames().stream())
                .toList();

        if (stage.getState() == JobState.SUCCESSFUL) {
            List<HostComponent> hostComponents = hostComponentRepository
                    .findAllByComponentClusterIdAndComponentComponentNameAndHostHostnameIn(clusterId, componentName, hostnames);
            Service service = hostComponents.get(0).getComponent().getService();

            // Update the state of the host component
            hostComponents.forEach(hostComponent -> hostComponent.setState(MaintainState.STOPPED));
            hostComponentRepository.saveAll(hostComponents);

            if (hostComponents.stream().allMatch(x -> x.getState() == MaintainState.STOPPED)) {
                service.setState(MaintainState.STOPPED);
            }
            serviceRepository.save(service);
        }
    }

}
