package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.CommandType;
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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
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
        CommandType commandType = commandDTO.getCommandType();

        if (commandType == CommandType.SERVICE_INSTALL) {
            // service install
            installService(commandDTO);
        } else if (commandType == CommandType.HOST_INSTALL) {
            // host install
            installHostComponent(commandDTO);
        }

        Job job = commandJobFactory.createJob(commandDTO);

        CommandEvent commandEvent = new CommandEvent(commandDTO);
        commandEvent.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(commandEvent);

        return JobMapper.INSTANCE.Entity2CommandVO(job);
    }

    private void installHostComponent(CommandDTO commandDTO) {
        List<String> componentNameList = commandDTO.getComponentNames();
        String clusterName = commandDTO.getClusterName();
        String hostname = commandDTO.getHostname();
        //Persist hostComponent to database
        List<Component> componentList = componentRepository.findAllByClusterClusterNameAndComponentNameIn(clusterName, componentNameList);
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
        String clusterName = commandDTO.getClusterName();
        String stackName = commandDTO.getStackName();
        String stackVersion = commandDTO.getStackVersion();
        Map<String, Set<String>> componentHostMapping = commandDTO.getComponentHosts();
        List<ConfigurationDTO> serviceConfigs = commandDTO.getServiceConfigs();
        Map<String, List<ConfigDataDTO>> configDataDTOMap = new HashMap<>();
        if (serviceConfigs != null) {
            configDataDTOMap = serviceConfigs.stream().collect(Collectors.toMap(ConfigurationDTO::getServiceName, ConfigurationDTO::getConfigurations));
        }

        Map<String, ImmutablePair<StackDTO, List<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

        ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));
        List<ServiceDTO> serviceDTOSet = immutablePair.getRight();

        // Persist service, component and hostComponent metadata to database
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());
        for (ServiceDTO serviceDTO : serviceDTOSet) {
            String serviceName = serviceDTO.getServiceName();
            if (serviceNameList.contains(serviceName)) {
                // 1. Persist service
                Service service = ServiceMapper.INSTANCE.DTO2Entity(serviceDTO, cluster);
                Optional<Service> serviceOptional = serviceRepository.findByServiceName(serviceName);
                if (serviceOptional.isPresent()) {
                    service.setId(serviceOptional.get().getId());
                }
                service = serviceRepository.save(service);

                // 2. Initial config
                List<ConfigDataDTO> configDataDTOList = configDataDTOMap.get(serviceName);
                initialConfig(cluster, service, configDataDTOList);

                List<ComponentDTO> componentDTOList = serviceDTO.getComponents();
                for (ComponentDTO componentDTO : componentDTOList) {
                    String componentName = componentDTO.getComponentName();

                    // 3. Persist component
                    Component component = ComponentMapper.INSTANCE.DTO2Entity(componentDTO, service, cluster);
                    Optional<Component> componentOptional = componentRepository.findByClusterClusterNameAndComponentName(clusterName, componentName);
                    if (componentOptional.isPresent()) {
                        component.setId(componentOptional.get().getId());
                    }
                    component = componentRepository.save(component);

                    // 4. Persist hostComponent
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
    }

    private void initialConfig(Cluster cluster, Service service, List<ConfigDataDTO> configDataDTOList) {
        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();
        String serviceName = service.getServiceName();
        Map<String, ConfigDataDTO> configDataDTOMap = new HashMap<>();
        if (configDataDTOList != null) {
            configDataDTOMap = configDataDTOList.stream().collect(Collectors.toMap(ConfigDataDTO::getTypeName, configDataDTO -> configDataDTO));
        }

        //ServiceConfigRecord
        String defaultConfigDesc = MessageFormat.format("Initial config {0} for cluster {1}", serviceName, cluster.getClusterName());
        ServiceConfigRecord serviceConfigRecord = configurationManager.saveConfigRecord(cluster, service, defaultConfigDesc).left;

        //ServiceConfig
        Map<String, Set<ConfigDataDTO>> serviceConfigMap = StackUtils.getStackConfigMap().get(StackUtils.fullStackName(stackName, stackVersion));
        for (ConfigDataDTO configDataDTO : serviceConfigMap.get(serviceName)) {
            String typeName = configDataDTO.getTypeName();
            Map<String, Object> configData = configDataDTO.getConfigData();
            Map<String, Map<String, Object>> configAttributes = configDataDTO.getConfigAttributes();
            if (configDataDTOMap.containsKey(typeName)) {
                configData = configDataDTOMap.get(typeName).getConfigData();
                configAttributes = configDataDTOMap.get(typeName).getConfigAttributes();
            }

            ServiceConfig serviceConfig = configurationManager.upsertConfig(cluster, service, typeName, configData, configAttributes);

            //ServiceConfigMapping
            ServiceConfigMapping serviceConfigMapping = new ServiceConfigMapping();
            serviceConfigMapping.setServiceConfig(serviceConfig);
            serviceConfigMapping.setServiceConfigRecord(serviceConfigRecord);
            serviceConfigMappingRepository.save(serviceConfigMapping);
        }
    }
}
