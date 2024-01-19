package org.apache.bigtop.manager.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.job.event.CommandEvent;
import org.apache.bigtop.manager.server.job.factory.JobContext;
import org.apache.bigtop.manager.server.job.factory.JobFactories;
import org.apache.bigtop.manager.server.job.factory.JobFactory;
import org.apache.bigtop.manager.server.job.validator.ValidatorExecutionChain;
import org.apache.bigtop.manager.server.job.validator.ValidatorContext;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.service.CommandService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@org.springframework.stereotype.Service
public class CommandServiceImpl implements CommandService {

    @Override
    @Transactional
    public CommandVO command(CommandDTO commandDTO) {
        CommandIdentifier commandIdentifier = new CommandIdentifier(commandDTO.getCommandLevel(), commandDTO.getCommand());

        // Validate command params
        ValidatorContext validatorContext = new ValidatorContext();
        validatorContext.setCommandDTO(commandDTO);
        ValidatorExecutionChain.execute(validatorContext, commandIdentifier);

        // Create job
        JobContext jobContext = new JobContext();
        jobContext.setCommandDTO(commandDTO);
        JobFactory jobFactory = JobFactories.getJobFactory(commandIdentifier);
        Job job = jobFactory.createJob(jobContext);

        // Publish command event
        CommandEvent event = new CommandEvent(commandDTO);
        event.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(event);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }
}
