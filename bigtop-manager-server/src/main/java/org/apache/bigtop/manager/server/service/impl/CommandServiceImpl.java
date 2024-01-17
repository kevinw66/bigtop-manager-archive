package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.listener.factory.CommandJobFactory;
import org.apache.bigtop.manager.server.listener.factory.JobFactoryContext;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Service;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;
import org.apache.bigtop.manager.server.service.CommandService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
public class CommandServiceImpl implements CommandService {

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private CommandJobFactory commandJobFactory;

    @Override
    @Transactional
    public CommandVO command(CommandDTO commandDTO) {
        validRequiredServices(commandDTO);

        JobFactoryContext jobFactoryContext = new JobFactoryContext();
        jobFactoryContext.setCommandDTO(commandDTO);
        Job job = commandJobFactory.createJob(jobFactoryContext);
        CommandEvent commandEvent = new CommandEvent(commandDTO);
        commandEvent.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(commandEvent);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }

    private void validRequiredServices(CommandDTO commandDTO) {
        if (commandDTO.getCommandLevel() == CommandLevel.SERVICE && commandDTO.getCommand() == Command.INSTALL) {
            List<ServiceCommandDTO> serviceCommands = commandDTO.getServiceCommands();

            Long clusterId = commandDTO.getClusterId();
            Cluster cluster = clusterRepository.getReferenceById(clusterId);
            String stackName = cluster.getStack().getStackName();
            String stackVersion = cluster.getStack().getStackVersion();
            Map<String, ImmutablePair<StackDTO, List<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();
            ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));
            Map<String, ServiceDTO> serviceNameToDTO = immutablePair.getRight()
                    .stream()
                    .collect(Collectors.toMap(ServiceDTO::getServiceName, Function.identity()));

            List<String> serviceNames = serviceCommands.stream().map(ServiceCommandDTO::getServiceName).toList();
            for (ServiceCommandDTO serviceCommand : serviceCommands) {
                String serviceName = serviceCommand.getServiceName();
                ServiceDTO serviceDTO = serviceNameToDTO.get(serviceName);
                List<String> requiredServices = serviceDTO.getRequiredServices();
                if (CollectionUtils.isEmpty(requiredServices)) {
                    return;
                }
                List<Service> serviceList = serviceRepository.findByClusterIdAndServiceNameIn(clusterId, requiredServices);
                List<String> list = serviceList.stream().map(Service::getServiceName).toList();

                requiredServices.removeAll(list);

                if (!serviceNames.containsAll(requiredServices)) {
                    throw new ApiException(ApiExceptionEnum.SERVICE_REQUIRED_NOT_FOUND, String.join(",", requiredServices));
                }

            }

        }
    }
}
