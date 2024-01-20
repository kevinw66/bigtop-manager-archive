package org.apache.bigtop.manager.server.job.factory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.helper.HostCacheStageHelper;
import org.apache.bigtop.manager.server.job.helper.ServiceComponentStageHelper;
import org.apache.bigtop.manager.server.job.strategy.StageCallback;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;
import org.apache.bigtop.manager.server.service.ServiceService;
import org.apache.commons.lang3.SerializationUtils;

import java.util.List;

import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;

@Slf4j
@org.springframework.stereotype.Component
public class ServiceInstallJobFactory implements JobFactory, StageCallback {

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private ServiceService serviceService;

    @Resource
    private HostCacheStageHelper hostCacheStageHelper;

    @Resource
    private ServiceComponentStageHelper serviceComponentStageHelper;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.SERVICE, Command.INSTALL);
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
        if (commandDTO.getCommand() == Command.INSTALL) {
            stageOrder += 1;
            hostCacheStageHelper.createStage(job, cluster, stageOrder, this.getClass().getName(), JsonUtils.writeAsString(commandDTO));
        }

        // If the cache stage is successful, start services
        if (commandDTO.getCommand() == Command.INSTALL && commandDTO.getCommandLevel() == CommandLevel.SERVICE) {
            CommandDTO startCommandDTO = SerializationUtils.clone(commandDTO);
            startCommandDTO.setCommand(Command.START);
            startCommandDTO.setCommandLevel(CommandLevel.INTERNAL_SERVICE_INSTALL);
            stageOrder = serviceComponentStageHelper.createStage(job, startCommandDTO, stageOrder, this.getClass().getName());

            // The check action needs to be executed by a single node
            CommandDTO checkCommandDTO = SerializationUtils.clone(commandDTO);
            checkCommandDTO.setCommand(Command.CHECK);
            checkCommandDTO.setCommandLevel(CommandLevel.INTERNAL_SERVICE_INSTALL);
            serviceComponentStageHelper.createStage(job, checkCommandDTO, stageOrder, this.getClass().getName());
        }

        return job;
    }


    @Override
    public void beforeStage(Stage stage) {
        CommandDTO commandDTO = JsonUtils.readFromString(stage.getPayload(), CommandDTO.class);
        if (stage.getName().equals(CACHE_STAGE_NAME) && commandDTO.getCommand() == Command.INSTALL && commandDTO.getCommandLevel() == CommandLevel.SERVICE) {
            serviceService.saveByCommand(commandDTO);
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

    @Override
    public void afterStage(Stage stage) {
        Long clusterId = stage.getCluster().getId();
        String componentName = stage.getComponentName();
        CommandDTO commandDTO = JsonUtils.readFromString(stage.getPayload(), CommandDTO.class);
        Command command = commandDTO.getCommand();

        if (stage.getState() == JobState.SUCCESSFUL && command == Command.START) {
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
