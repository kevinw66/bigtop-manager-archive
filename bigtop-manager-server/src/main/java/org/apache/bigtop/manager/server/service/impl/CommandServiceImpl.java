package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.dao.entity.Job;
import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.server.command.job.factory.JobContext;
import org.apache.bigtop.manager.server.command.job.factory.JobFactories;
import org.apache.bigtop.manager.server.command.job.factory.JobFactory;
import org.apache.bigtop.manager.server.command.job.validator.ValidatorContext;
import org.apache.bigtop.manager.server.command.job.validator.ValidatorExecutionChain;
import org.apache.bigtop.manager.server.command.scheduler.JobScheduler;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.service.CommandService;

@Slf4j
@org.springframework.stereotype.Service
public class CommandServiceImpl implements CommandService {

    @Resource
    private JobScheduler jobScheduler;

    @Override
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

        // Submit job
        jobScheduler.submit(job);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }
}
