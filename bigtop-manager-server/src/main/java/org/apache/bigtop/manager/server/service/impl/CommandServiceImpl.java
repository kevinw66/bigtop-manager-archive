package org.apache.bigtop.manager.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.factory.JobContext;
import org.apache.bigtop.manager.server.job.factory.JobFactories;
import org.apache.bigtop.manager.server.job.factory.JobFactory;
import org.apache.bigtop.manager.server.job.validator.ChainedCommandValidator;
import org.apache.bigtop.manager.server.job.validator.ValidatorContext;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.event.ClusterCreateEvent;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.event.HostAddEvent;
import org.apache.bigtop.manager.server.model.event.HostCacheEvent;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.service.CommandService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@org.springframework.stereotype.Service
public class CommandServiceImpl implements CommandService {

    @Override
    @Transactional
    public CommandVO command(CommandDTO commandDTO) {
        CommandIdentifier commandIdentifier = new CommandIdentifier(commandDTO.getCommandLevel(), commandDTO.getCommand());

        ValidatorContext validatorContext = new ValidatorContext();
        validatorContext.setCommandDTO(commandDTO);
        ChainedCommandValidator.validate(validatorContext, commandIdentifier);

        JobContext jobContext = new JobContext();
        jobContext.setCommandDTO(commandDTO);
        JobFactory jobFactory = JobFactories.getJobFactory(commandIdentifier);
        Job job = jobFactory.createJob(jobContext);

        createAndPublishEvent(job, commandDTO);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }

    /**
     * TODO need to optimize
     *
     * @param commandDTO {@link CommandDTO}
     */
    private void createAndPublishEvent(Job job, CommandDTO commandDTO) {
        if (commandDTO.getCommandLevel().equals(CommandLevel.SERVICE) && commandDTO.getCommand().equals(Command.INSTALL)) {
            CommandEvent commandEvent = new CommandEvent(commandDTO);
            commandEvent.setJobId(job.getId());
            SpringContextHolder.getApplicationContext().publishEvent(commandEvent);
        } else if (commandDTO.getCommandLevel().equals(CommandLevel.SERVICE) && commandDTO.getCommand().equals(Command.CONFIGURE)) {
            HostCacheEvent hostCacheEvent = new HostCacheEvent(commandDTO.getClusterId());
            hostCacheEvent.setJobId(job.getId());
            SpringContextHolder.getApplicationContext().publishEvent(hostCacheEvent);
        } else if (commandDTO.getCommandLevel().equals(CommandLevel.CLUSTER) && commandDTO.getCommand().equals(Command.INSTALL)) {
            ClusterDTO clusterDTO = new ClusterDTO();
            BeanUtils.copyProperties(commandDTO.getClusterCommand(), clusterDTO);
            ClusterCreateEvent clusterCreateEvent = new ClusterCreateEvent(clusterDTO);
            clusterCreateEvent.setJobId(job.getId());
            SpringContextHolder.getApplicationContext().publishEvent(clusterCreateEvent);
        } else if (commandDTO.getCommandLevel().equals(CommandLevel.HOST) && commandDTO.getCommand().equals(Command.INSTALL)) {
            HostAddEvent hostAddEvent = new HostAddEvent(null);
            hostAddEvent.setJobId(job.getId());
            SpringContextHolder.getApplicationContext().publishEvent(hostAddEvent);
        }
    }

}
