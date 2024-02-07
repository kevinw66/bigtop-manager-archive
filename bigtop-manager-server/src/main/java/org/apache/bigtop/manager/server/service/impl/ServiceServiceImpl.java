package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.MaintainState;
import org.apache.bigtop.manager.server.model.dto.*;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.vo.ServiceVO;
import org.apache.bigtop.manager.dao.entity.*;
import org.apache.bigtop.manager.dao.repository.*;
import org.apache.bigtop.manager.server.service.ConfigService;
import org.apache.bigtop.manager.server.service.ServiceService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private ConfigService configService;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Override
    public List<ServiceVO> list(Long clusterId) {
        List<Service> serviceList = serviceRepository.findAllByClusterId(clusterId);

        return ServiceMapper.INSTANCE.fromEntity2VO(serviceList);
    }

    @Override
    public ServiceVO get(Long id) {
        Service service = serviceRepository.findById(id).orElse(new Service());
        return ServiceMapper.INSTANCE.fromEntity2VO(service);
    }

    @Override
    public void saveByCommand(CommandDTO commandDTO) {
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
                configService.upsert(clusterId, serviceOptional.get().getId(), serviceCommand.getConfigs());
                continue;
            }

            // 1. Persist service
            ServiceDTO serviceDTO = serviceNameToDTO.get(serviceName);

            Service service = ServiceMapper.INSTANCE.fromDTO2Entity(serviceDTO, cluster);
            service = serviceRepository.save(service);

            // Init config for new installed service
            configService.upsert(clusterId, service.getId(), serviceCommand.getConfigs());

            Map<String, ComponentDTO> componentNameToDTO = serviceDTO.getComponents()
                    .stream()
                    .collect(Collectors.toMap(ComponentDTO::getComponentName, Function.identity()));
            for (ComponentHostDTO componentHostDTO : serviceCommand.getComponentHosts()) {
                String componentName = componentHostDTO.getComponentName();
                ComponentDTO componentDTO = componentNameToDTO.get(componentName);

                // 2. Persist component
                Component component = ComponentMapper.INSTANCE.fromDTO2Entity(componentDTO, service, cluster);
                Optional<Component> componentOptional = componentRepository.findByClusterIdAndComponentName(clusterId, componentName);
                if (componentOptional.isPresent()) {
                    component.setId(componentOptional.get().getId());
                }
                component = componentRepository.save(component);

                // 3. Persist hostComponent
                List<Host> hostList = hostRepository.findAllByClusterIdAndHostnameIn(cluster.getId(), componentHostDTO.getHostnames());
                for (Host host : hostList) {
                    HostComponent hostComponent = new HostComponent();
                    Optional<HostComponent> hostComponentOptional = hostComponentRepository.findByComponentComponentNameAndHostHostname(componentName, host.getHostname());
                    hostComponentOptional.ifPresent(value -> hostComponent.setId(value.getId()));
                    hostComponent.setHost(host);
                    hostComponent.setComponent(component);
                    hostComponent.setState(MaintainState.UNINSTALLED);
                    hostComponentRepository.save(hostComponent);
                }
            }
        }
    }

}
