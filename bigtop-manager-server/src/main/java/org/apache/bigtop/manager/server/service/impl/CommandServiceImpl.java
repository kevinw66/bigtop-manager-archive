package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ValidateType;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.listener.factory.CommandJobFactory;
import org.apache.bigtop.manager.server.listener.factory.JobFactoryContext;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.service.CommandService;
import org.apache.bigtop.manager.server.validate.ChainContext;
import org.apache.bigtop.manager.server.validate.ChainValidatorHandler;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@org.springframework.stereotype.Service
public class CommandServiceImpl implements CommandService {

    @Resource
    private CommandJobFactory commandJobFactory;

    @Override
    @Transactional
    public CommandVO command(CommandDTO commandDTO) {
        ChainContext chainContext = new ChainContext();
        chainContext.setCommandDTO(commandDTO);
        ChainValidatorHandler.handleRequest(chainContext, ValidateType.COMMAND);

        JobFactoryContext jobFactoryContext = new JobFactoryContext();
        jobFactoryContext.setCommandDTO(commandDTO);
        Job job = commandJobFactory.createJob(jobFactoryContext);
        CommandEvent commandEvent = new CommandEvent(commandDTO);
        commandEvent.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(commandEvent);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }

}
