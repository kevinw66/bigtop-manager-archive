package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.listener.factory.CommandJobFactory;
import org.apache.bigtop.manager.server.model.dto.*;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.service.CommandService;
import org.apache.bigtop.manager.server.stack.ConfigurationManager;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
public class CommandServiceImpl implements CommandService {

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ServiceConfigMappingRepository serviceConfigMappingRepository;

    @Resource
    private ConfigurationManager configurationManager;

    @Resource
    private CommandJobFactory commandJobFactory;

    @Override
    @Transactional
    public CommandVO command(CommandDTO commandDTO) {
        if (commandDTO.getCommand() == Command.INSTALL || commandDTO.getCommand() == Command.REINSTALL) {
            if (commandDTO.getCommand() == Command.REINSTALL) {
                commandDTO.setCommand(Command.INSTALL);
            }
            switch (commandDTO.getCommandLevel()) {
                case SERVICE -> installService(commandDTO);
                case HOST -> installHostComponent(commandDTO);
            }
        }

        Job job = commandJobFactory.createJob(commandDTO);
        CommandEvent commandEvent = new CommandEvent(commandDTO);
        commandEvent.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(commandEvent);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }

    private void installHostComponent(CommandDTO commandDTO) {
        List<String> componentNameList = commandDTO.getHostCommands().getComponentNames();
        String hostname = commandDTO.getHostCommands().getHostname();
        Long clusterId = commandDTO.getClusterId();

        // Persist hostComponent to database
        List<Component> componentList = componentRepository.findAllByClusterIdAndComponentNameIn(clusterId, componentNameList);
        Host host = hostRepository.findByHostname(hostname);
        for (Component component : componentList) {
            HostComponent hostComponent = new HostComponent();
            hostComponent.setHost(host);
            hostComponent.setComponent(component);
            hostComponent.setState(MaintainState.UNINSTALLED);

            Optional<HostComponent> hostComponentOptional = hostComponentRepository.findByComponentComponentNameAndHostHostname(component.getComponentName(), host.getHostname());
            hostComponentOptional.ifPresent(value -> hostComponent.setId(value.getId()));
            hostComponentRepository.save(hostComponent);
        }
    }

    private void installService(CommandDTO commandDTO) {
        log.info("Enter install method");
        List<ServiceCommandDTO> installServiceCommands = new ArrayList<>();

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

        // Persist service, component and hostComponent metadata to database
        for (ServiceCommandDTO serviceCommand : serviceCommands) {
            String serviceName = serviceCommand.getServiceName();
            Optional<Service> serviceOptional = serviceRepository.findByClusterIdAndServiceName(clusterId, serviceName);
            if (serviceOptional.isPresent()) {
                // Service already installed, update config if changed
                upsertConfig(cluster, serviceOptional.get(), serviceCommand);
                continue;
            }
            installServiceCommands.add(serviceCommand);

            // 1. Persist service
            ServiceDTO serviceDTO = serviceNameToDTO.get(serviceName);
            Service service = ServiceMapper.INSTANCE.fromDTO2Entity(serviceDTO, cluster);
            service.setState(MaintainState.UNINSTALLED);
            service = serviceRepository.save(service);

            List<String> requiredServices = serviceDTO.getRequiredServices();
            validRequiredServices(requiredServices, clusterId);

            // Init config for new installed service
            upsertConfig(cluster, service, serviceCommand);

            Map<String, ComponentDTO> componentNameToDTO = serviceDTO.getComponents()
                    .stream()
                    .collect(Collectors.toMap(ComponentDTO::getComponentName, Function.identity()));
            for (ComponentHostDTO componentHostDTO : serviceCommand.getComponentHosts()) {
                String componentName = componentHostDTO.getComponentName();
                ComponentDTO componentDTO = componentNameToDTO.get(componentName);

                // 2. Persist component
                Component component = ComponentMapper.INSTANCE.fromDTO2Entity(componentDTO, service, cluster);
                component = componentRepository.save(component);

                // 3. Persist hostComponent
                List<Host> hostList = hostRepository.findAllByClusterIdAndHostnameIn(cluster.getId(), componentHostDTO.getHostnames());
                for (Host host : hostList) {
                    HostComponent hostComponent = new HostComponent();
                    hostComponent.setHost(host);
                    hostComponent.setComponent(component);
                    hostComponent.setState(MaintainState.UNINSTALLED);
                    hostComponentRepository.save(hostComponent);
                }
            }
        }
        commandDTO.setServiceCommands(installServiceCommands);
    }

    private void upsertConfig(Cluster cluster, Service service, ServiceCommandDTO serviceCommandDTO) {
        // Save config record
        String configDesc = "Initial config for " + serviceCommandDTO.getServiceName();
        ServiceConfigRecord serviceConfigRecord = configurationManager.saveConfigRecord(cluster, service, configDesc).left;

        for (ConfigDataDTO configDataDTO : serviceCommandDTO.getConfigs()) {
            String typeName = configDataDTO.getTypeName();
            List<PropertyDTO> properties = configDataDTO.getProperties();

            ServiceConfig serviceConfig = configurationManager.upsertConfig(cluster, service, typeName, properties);

            // Save config mapping
            ServiceConfigMapping serviceConfigMapping = new ServiceConfigMapping();
            serviceConfigMapping.setServiceConfig(serviceConfig);
            serviceConfigMapping.setServiceConfigRecord(serviceConfigRecord);
            serviceConfigMappingRepository.save(serviceConfigMapping);
        }
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
