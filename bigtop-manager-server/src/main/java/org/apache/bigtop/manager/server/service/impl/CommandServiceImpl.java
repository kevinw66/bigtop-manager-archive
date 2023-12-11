package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.listener.factory.CommandJobFactory;
import org.apache.bigtop.manager.server.model.dto.*;
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

import java.util.*;
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
        CommandLevel commandType = commandDTO.getCommandLevel();
        Command command = commandDTO.getCommand();
        if (command == Command.REINSTALL) {
            commandDTO.setCommand(Command.INSTALL);
        }

        if (commandType == CommandLevel.SERVICE && command == Command.INSTALL) {
            // service install
            installService(commandDTO);
        } else if (commandType == CommandLevel.HOST && command == Command.INSTALL) {
            // host install
            installHostComponent(commandDTO);
        }

        Job job = commandJobFactory.createJob(commandDTO);

        CommandEvent commandEvent = new CommandEvent(commandDTO);
        commandEvent.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(commandEvent);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }

    private void installHostComponent(CommandDTO commandDTO) {
        List<String> componentNameList = commandDTO.getComponentNames();
        String hostname = commandDTO.getHostname();
        Long clusterId = commandDTO.getClusterId();
        //Persist hostComponent to database
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
        List<String> serviceNameList = commandDTO.getServiceNames();
        Long clusterId = commandDTO.getClusterId();
        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();
        Map<String, Set<String>> componentHostMapping = commandDTO.getComponentHosts();
        Map<String, Service> serviceMap = new HashMap<>();

        Map<String, ImmutablePair<StackDTO, List<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

        ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));
        List<ServiceDTO> serviceDTOSet = immutablePair.getRight();

        // Persist service, component and hostComponent metadata to database
        for (ServiceDTO serviceDTO : serviceDTOSet) {
            String serviceName = serviceDTO.getServiceName();
            if (serviceNameList.contains(serviceName)) {
                // 1. Persist service
                Service service = ServiceMapper.INSTANCE.fromDTO2Entity(serviceDTO, cluster);
                Optional<Service> serviceOptional = serviceRepository.findByClusterIdAndServiceName(clusterId, serviceName);
                if (serviceOptional.isPresent()) {
                    service.setId(serviceOptional.get().getId());
                }
                service.setState(MaintainState.UNINSTALLED);
                service = serviceRepository.save(service);
                serviceMap.put(serviceName, service);

                List<ComponentDTO> componentDTOList = serviceDTO.getComponents();
                for (ComponentDTO componentDTO : componentDTOList) {
                    String componentName = componentDTO.getComponentName();

                    // 2. Persist component
                    Component component = ComponentMapper.INSTANCE.fromDTO2Entity(componentDTO, service, cluster);
                    Optional<Component> componentOptional = componentRepository.findByClusterIdAndComponentName(clusterId, componentName);
                    if (componentOptional.isPresent()) {
                        component.setId(componentOptional.get().getId());
                    }
                    component = componentRepository.save(component);

                    // 3. Persist hostComponent
                    Set<String> hostSet = componentHostMapping.get(componentName);
                    if (hostSet == null) {
                        throw new ApiException(ApiExceptionEnum.HOST_NOT_FOUND);
                    }
                    List<Host> hostList = hostRepository.findAllByHostnameIn(hostSet);
                    for (Host host : hostList) {
                        HostComponent hostComponent = new HostComponent();
                        hostComponent.setHost(host);
                        hostComponent.setComponent(component);
                        hostComponent.setState(MaintainState.UNINSTALLED);

                        Optional<HostComponent> hostComponentOptional = hostComponentRepository.findByComponentComponentNameAndHostHostname(componentName, host.getHostname());
                        hostComponentOptional.ifPresent(value -> hostComponent.setId(value.getId()));
                        hostComponentRepository.save(hostComponent);
                    }
                }
            }
        }

        // 4. Initial or update config
        List<ConfigurationDTO> serviceConfigs = commandDTO.getServiceConfigs();
        Map<String, ConfigurationDTO> configurationDTOMap = new HashMap<>();
        if (serviceConfigs != null) {
            configurationDTOMap = serviceConfigs.stream().collect(Collectors.toMap(ConfigurationDTO::getServiceName, x -> x));
        }
        for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
            String serviceName = entry.getKey();
            Service service = entry.getValue();
            initialConfig(cluster, service, configurationDTOMap.get(serviceName));
        }

        for (ConfigurationDTO configurationDTO : commandDTO.getServiceConfigs()) {
            String serviceName = configurationDTO.getServiceName();
            if (!serviceMap.containsKey(serviceName)) {
                serviceRepository.findByClusterIdAndServiceName(cluster.getId(), serviceName)
                        .ifPresent(service -> initialConfig(cluster, service, configurationDTO));
            }
        }
    }

    private void initialConfig(Cluster cluster, Service service, ConfigurationDTO configurationDTO) {
        Map<String, ConfigDataDTO> configDataDTOMap = new HashMap<>();
        String configDesc = "Initial configuration for " + service.getServiceName();
        if (configurationDTO != null) {
            List<ConfigDataDTO> configurations = configurationDTO.getConfigurations();
            if (CollectionUtils.isEmpty(configurations)) {
                configDataDTOMap = configurations.stream().collect(Collectors.toMap(ConfigDataDTO::getTypeName, configDataDTO -> configDataDTO));
            }
            configDesc = configurationDTO.getConfigDesc();
        }

        //ServiceConfigRecord
        ServiceConfigRecord serviceConfigRecord = configurationManager.saveConfigRecord(cluster, service, configDesc).left;

        Map<String, Set<ConfigDataDTO>> initServiceConfigMap = StackUtils.getStackConfigMap().get(StackUtils.fullStackName(cluster.getStack().getStackName(), cluster.getStack().getStackVersion()));
        for (ConfigDataDTO configDataDTO : initServiceConfigMap.get(service.getServiceName())) {
            String typeName = configDataDTO.getTypeName();
            List<PropertyDTO> properties = configDataDTO.getProperties();
            Map<String, String> attributes = configDataDTO.getAttributes();
            if (configDataDTOMap.containsKey(typeName)) {
                properties = configDataDTOMap.get(typeName).getProperties();
                attributes = configDataDTOMap.get(typeName).getAttributes();
            }

            ServiceConfig serviceConfig = configurationManager.upsertConfig(cluster, service, typeName, properties, attributes);
            //ServiceConfigMapping
            ServiceConfigMapping serviceConfigMapping = new ServiceConfigMapping();
            serviceConfigMapping.setServiceConfig(serviceConfig);
            serviceConfigMapping.setServiceConfigRecord(serviceConfigRecord);
            serviceConfigMappingRepository.save(serviceConfigMapping);
        }
    }
}
