package org.apache.bigtop.manager.server.job.validator;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Service;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequiredServicesValidator implements CommandValidator {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private ServiceRepository serviceRepository;

    @Override
    public List<CommandIdentifier> getCommandIdentifiers() {
        return List.of(new CommandIdentifier(CommandLevel.SERVICE, Command.INSTALL));
    }

    @Override
    public void validate(ValidatorContext context) {
        CommandDTO commandDTO = context.getCommandDTO();
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
