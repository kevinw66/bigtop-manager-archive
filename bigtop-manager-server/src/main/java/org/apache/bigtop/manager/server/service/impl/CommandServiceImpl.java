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
        Map<String, Service> serviceMap = new HashMap<>();

        Map<String, ImmutablePair<StackDTO, List<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

        ImmutablePair<StackDTO, List<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));
        List<ServiceDTO> serviceDTOSet = immutablePair.getRight();

        // Persist service, component and hostComponent metadata to database
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());
        for (ServiceDTO serviceDTO : serviceDTOSet) {
            String serviceName = serviceDTO.getServiceName();
            if (serviceNameList.contains(serviceName)) {
                // 1. Persist service
                Service service = ServiceMapper.INSTANCE.fromDTO2Entity(serviceDTO, cluster);
                Optional<Service> serviceOptional = serviceRepository.findByClusterClusterNameAndServiceName(clusterName, serviceName);
                if (serviceOptional.isPresent()) {
                    service.setId(serviceOptional.get().getId());
                }
                service.setState(MaintainState.UNINSTALLED);
                service = serviceRepository.save(service);
                serviceMap.put(serviceName, service);

                List<ComponentDTO> componentDTOList = serviceDTO.getComponents();
                for (ComponentDTO componentDTO : componentDTOList) {
                    String componentName = componentDTO.getComponentName();

                    // 3. Persist component
                    Component component = ComponentMapper.INSTANCE.fromDTO2Entity(componentDTO, service, cluster);
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

        // 2. Initial config
        for (ConfigurationDTO configurationDTO : commandDTO.getServiceConfigs()) {
            serviceMap.get(configurationDTO.getServiceName());
            initialConfig(cluster, serviceMap, configurationDTO);
        }
    }

    private void initialConfig(Cluster cluster, Map<String, Service> serviceMap, ConfigurationDTO configurationDTO) {
        String serviceName = configurationDTO.getServiceName();
        Service service = serviceMap.containsKey(serviceName) ? serviceMap.get(serviceName) :
                serviceRepository.findByClusterClusterNameAndServiceName(cluster.getClusterName(), serviceName).orElse(new Service());

        //ServiceConfigRecord
        String configDesc = configurationDTO.getConfigDesc();
        ImmutablePair<ServiceConfigRecord, List<ServiceConfigMapping>> immutablePair = configurationManager.saveConfigRecord(cluster, service, configDesc);
        ServiceConfigRecord serviceConfigRecord = immutablePair.left;
        //ServiceConfig
        for (ConfigDataDTO configDataDTO : configurationDTO.getConfigurations()) {
            String typeName = configDataDTO.getTypeName();
            List<PropertyDTO> properties = configDataDTO.getProperties();
            Map<String, String> attributes = configDataDTO.getAttributes();

            ServiceConfig serviceConfig = configurationManager.upsertConfig(cluster, service, typeName, properties, attributes);

            //ServiceConfigMapping
            ServiceConfigMapping serviceConfigMapping = new ServiceConfigMapping();
            serviceConfigMapping.setServiceConfig(serviceConfig);
            serviceConfigMapping.setServiceConfigRecord(serviceConfigRecord);
            serviceConfigMappingRepository.save(serviceConfigMapping);
        }
    }
}
