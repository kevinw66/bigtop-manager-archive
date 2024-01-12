package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.listener.factory.CommandJobFactory;
import org.apache.bigtop.manager.server.listener.factory.JobFactoryContext;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Service;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;
import org.apache.bigtop.manager.server.service.CommandService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
public class CommandServiceImpl implements CommandService {

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private CommandJobFactory commandJobFactory;

    @Override
    @Transactional
    public CommandVO command(CommandDTO commandDTO) {

        JobFactoryContext jobFactoryContext = new JobFactoryContext();
        jobFactoryContext.setCommandDTO(commandDTO);
        Job job = commandJobFactory.createJob(jobFactoryContext);
        CommandEvent commandEvent = new CommandEvent(commandDTO);
        commandEvent.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(commandEvent);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }

    private void validRequiredServices(List<String> requiredServices, Long clusterId) {
        if (CollectionUtils.isEmpty(requiredServices)) {
            return;
        }
        List<Service> serviceList = serviceRepository.findByClusterIdAndServiceNameIn(clusterId, requiredServices);
        if (serviceList.size() != requiredServices.size()) {
            throw new ApiException(ApiExceptionEnum.SERVICE_REQUIRED_NOT_FOUND, String.join(",", requiredServices));
        }
    }
}
