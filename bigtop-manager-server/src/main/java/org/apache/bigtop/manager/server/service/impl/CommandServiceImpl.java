package org.apache.bigtop.manager.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.ValidateType;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.listener.factory.JobContext;
import org.apache.bigtop.manager.server.listener.factory.JobFactories;
import org.apache.bigtop.manager.server.listener.factory.JobFactory;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.event.ClusterCreateEvent;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.event.HostAddEvent;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.service.CommandService;
import org.apache.bigtop.manager.server.validate.ChainValidatorHandler;
import org.apache.bigtop.manager.server.validate.ValidatorContext;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@org.springframework.stereotype.Service
public class CommandServiceImpl implements CommandService {

    @Override
    @Transactional
    public CommandVO command(CommandDTO commandDTO) {
        ValidatorContext validatorContext = new ValidatorContext();
        validatorContext.setCommandDTO(commandDTO);
        ChainValidatorHandler.handle(validatorContext, generateValidateType(commandDTO));

        JobContext jobContext = new JobContext();
        jobContext.setCommandDTO(commandDTO);
        JobFactory jobFactory = JobFactories.getJobFactory(commandDTO.getCommandLevel());
        Job job = jobFactory.createJob(jobContext);

        createAndPublishEvent(job, commandDTO);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }

    /**
     * generate validate type
     * TODO Maybe we can create a class called CommandIdentifier, so both factory and validator can use it?
     *
     * @param commandDTO {@link CommandDTO}
     * @return {@link ValidateType}
     */
    private ValidateType generateValidateType(CommandDTO commandDTO) {
        return ValidateType.valueOf(commandDTO.getCommandLevel().name() + "_" + commandDTO.getCommand().name());
    }

    /**
     * TODO need to optimize
     *
     * @param commandDTO {@link CommandDTO}
     */
    private void createAndPublishEvent(Job job, CommandDTO commandDTO) {
        if (commandDTO.getCommandLevel().equals(CommandLevel.SERVICE)) {
            CommandEvent commandEvent = new CommandEvent(commandDTO);
            commandEvent.setJobId(job.getId());
            SpringContextHolder.getApplicationContext().publishEvent(commandEvent);
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
