package org.apache.bigtop.manager.server.service.impl;

import com.google.common.eventbus.EventBus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.stack.StackConfigUtils;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.StatusType;
import org.apache.bigtop.manager.server.enums.heartbeat.CommandState;
import org.apache.bigtop.manager.server.model.dto.*;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.mapper.*;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.ServiceVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.bigtop.manager.server.service.ServiceService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

@Slf4j
@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private ServiceConfigRepository serviceConfigRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ServiceConfigRecordRepository serviceConfigRecordRepository;

    @Resource
    private ServiceConfigMappingRepository serviceConfigMappingRepository;

    @Resource
    private EventBus eventBus;

    @Resource
    private HostService hostService;

    @Override
    public List<ServiceVO> list() {
        List<ServiceVO> serviceVOList = new ArrayList<>();
        serviceRepository.findAll().forEach(stack -> {
            ServiceVO serviceVO = ServiceMapper.INSTANCE.Entity2VO(stack);
            serviceVOList.add(serviceVO);
        });

        return serviceVOList;
    }

    @Override
    public ServiceVO get(Long id) {
        Service service = serviceRepository.findById(id).orElse(new Service());
        return ServiceMapper.INSTANCE.Entity2VO(service);
    }

    @Override
    public List<HostComponentVO> hostComponent(Long id) {
        List<HostComponent> hostComponentList = hostComponentRepository.findAllByComponentServiceId(id);
        return HostComponentMapper.INSTANCE.Entity2VO(hostComponentList);
    }

    @Override
    public CommandVO command(CommandDTO commandDTO) {
        Command command = commandDTO.getCommand();
        String clusterName = commandDTO.getClusterName();
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());

        //persist request to database
        Job job = JobMapper.INSTANCE.DTO2Entity(commandDTO, cluster);
        job = jobRepository.save(job);

        if (command == Command.INSTALL) {
            install(commandDTO);
            // cache
            hostService.cache(cluster.getId());
        }
        CommandEvent commandEvent = CommandMapper.INSTANCE.DTO2Event(commandDTO, job);
        eventBus.post(commandEvent);

        return JobMapper.INSTANCE.Entity2VO(job);
    }


    private void install(CommandDTO commandDTO) {
        log.info("Enter install method");
        List<String> serviceNameList = commandDTO.getServiceNames();
        String clusterName = commandDTO.getClusterName();
        String stackName = commandDTO.getStackName();
        String stackVersion = commandDTO.getStackVersion();
        Map<String, Set<String>> componentHostMapping = commandDTO.getComponentHosts();

        Map<String, ImmutablePair<StackDTO, Set<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();

        ImmutablePair<StackDTO, Set<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));
        Set<ServiceDTO> serviceDTOSet = immutablePair.getRight();

        // Persist service, component and hostComponent metadata to database
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());
        for (ServiceDTO serviceDTO : serviceDTOSet) {
            String serviceName = serviceDTO.getServiceName();

            // 1. Persist service
            Service service = ServiceMapper.INSTANCE.DTO2Entity(serviceDTO, cluster);
            Optional<Service> serviceOptional = serviceRepository.findByServiceName(serviceName);
            if (serviceOptional.isPresent()) {
                service.setId(serviceOptional.get().getId());
            }
            service = serviceRepository.save(service);

            // 2. Initial config
            initialConfig(cluster, service);

            if (serviceNameList.contains(serviceName)) {
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
                    List<Host> hostList = hostRepository.findAllByHostnameIn(hostSet);
                    for (Host host : hostList) {
                        HostComponent hostComponent = new HostComponent();
                        hostComponent.setHost(host);
                        hostComponent.setComponent(component);
                        hostComponent.setStatus(StatusType.UNINSTALLED.getCode());
                        hostComponent.setState(CommandState.UNINSTALLED);

                        Optional<HostComponent> hostComponentOptional = hostComponentRepository.findByComponentComponentNameAndHostHostname(componentName, host.getHostname());
                        hostComponentOptional.ifPresent(value -> hostComponent.setId(value.getId()));
                        hostComponentRepository.save(hostComponent);
                    }
                }
            }
        }
    }

    private void initialConfig(Cluster cluster, Service service) {
        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();
        String serviceName = service.getServiceName();

        //ServiceConfigRecord
        ServiceConfigRecord latestServiceConfigRecord = serviceConfigRecordRepository.findFirstByClusterIdAndServiceIdOrderByVersionDesc(cluster.getId(), service.getId())
                .orElse(new ServiceConfigRecord());

        ServiceConfigRecord serviceConfigRecord = new ServiceConfigRecord();
        if (latestServiceConfigRecord.getId() != null) {
            serviceConfigRecord.setVersion(latestServiceConfigRecord.getVersion() + 1);
        } else {
            serviceConfigRecord.setVersion(1);
        }
        serviceConfigRecord.setConfigDesc("Initial config " + serviceName + " for " + cluster.getClusterName() + " cluster");
        serviceConfigRecord.setService(service);
        serviceConfigRecord.setCluster(cluster);
        serviceConfigRecord = serviceConfigRecordRepository.save(serviceConfigRecord);

        //ServiceConfig
        Map<String, Map<String, Set<String>>> stackConfigMap = StackUtils.getStackConfigMap();
        Map<String, Set<String>> serviceConfigMap = stackConfigMap.get(StackUtils.fullStackName(stackName, stackVersion));
        for (String configPath : serviceConfigMap.get(serviceName)) {
            String typeName = configPath.substring(configPath.lastIndexOf("/") + 1, configPath.lastIndexOf("."));
            String configData = JsonUtils.writeAsString(StackConfigUtils.loadConfig(configPath));

            ServiceConfig latestServiceConfig = serviceConfigRepository.findFirstByClusterIdAndServiceIdAndTypeNameOrderByVersionDesc(cluster.getId(), service.getId(), typeName)
                    .orElse(new ServiceConfig());

            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.setService(service);
            serviceConfig.setCluster(cluster);
            serviceConfig.setTypeName(typeName);
            serviceConfig.setConfigData(configData);

            if (latestServiceConfig.getId() == null) {
                log.info("insert serviceConfig");
                serviceConfig.setVersion(1);
                serviceConfig = serviceConfigRepository.save(serviceConfig);
            } else if (!configData.equals(latestServiceConfig.getConfigData())) {
                log.info("update serviceConfig");
                serviceConfig.setVersion(latestServiceConfig.getVersion() + 1);
                serviceConfig = serviceConfigRepository.save(serviceConfig);
            } else {
                serviceConfig = latestServiceConfig;
                log.info("don't need update serviceConfig");
            }

            //ServiceConfigMapping
            ServiceConfigMapping serviceConfigMapping = new ServiceConfigMapping();
            serviceConfigMapping.setServiceConfig(serviceConfig);
            serviceConfigMapping.setServiceConfigRecord(serviceConfigRecord);
            serviceConfigMappingRepository.save(serviceConfigMapping);
        }
    }
}
