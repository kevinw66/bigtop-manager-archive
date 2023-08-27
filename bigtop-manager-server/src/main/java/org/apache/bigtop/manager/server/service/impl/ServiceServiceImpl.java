package org.apache.bigtop.manager.server.service.impl;

import com.google.common.eventbus.EventBus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.stack.StackConfigUtils;
import org.apache.bigtop.manager.server.enums.CommandEvent;
import org.apache.bigtop.manager.server.enums.RequestState;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.RequestMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.*;
import org.apache.bigtop.manager.server.orm.repository.*;
import org.apache.bigtop.manager.server.service.ServiceService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private RequestRepository requestRepository;

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
    private EventBus eventBus;

    @Override
    public CommandVO command(CommandDTO commandDTO) {
        CommandEvent commandEvent = CommandEvent.valueOf(commandDTO.getCommand());
        String clusterName = commandDTO.getClusterName();


        if (commandEvent == CommandEvent.INSTALL) {
            install(commandDTO);
        }

        eventBus.post(commandDTO);

        //persist request to database
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());
        Request request = RequestMapper.INSTANCE.DTO2Entity(commandDTO, cluster);
        request.setState(RequestState.PENDING.name());
        request = requestRepository.save(request);

        return RequestMapper.INSTANCE.Entity2VO(request);
    }


    private void install(CommandDTO commandDTO) {
        System.out.println("Enter install");
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

            // Persist serviceConfig
            Map<String, Map<String, Set<String>>> stackConfigMap = StackUtils.getStackConfigMap();
            Map<String, Set<String>> serviceConfigMap = stackConfigMap.get(StackUtils.fullStackName(stackName, stackVersion));
            for (String configPath : serviceConfigMap.get(serviceName)) {
                Map<String, Object> loadedConfig = StackConfigUtils.loadConfig(configPath);
                String typeName = configPath.substring(configPath.lastIndexOf("/") + 1, configPath.lastIndexOf("."));

                Integer savedMaxVersion = serviceConfigRepository.findMaxVersion(cluster.getId(), service.getId(), typeName).orElse(0);
                ServiceConfig serviceConfig = new ServiceConfig();
                serviceConfig.setVersion(savedMaxVersion + 1);
                serviceConfig.setTypeName(typeName);
                serviceConfig.setConfigData(JsonUtils.object2String(loadedConfig));
                serviceConfig.setService(service);
                serviceConfig.setCluster(cluster);
                serviceConfig.setStatus(true);

                serviceConfigRepository.save(serviceConfig);

            }

            if (serviceNameList.contains(serviceName)) {
                List<ComponentDTO> componentDTOList = serviceDTO.getComponents();
                for (ComponentDTO componentDTO : componentDTOList) {
                    String componentName = componentDTO.getComponentName();

                    // 2. Persist component
                    Component component = ComponentMapper.INSTANCE.DTO2Entity(componentDTO, service, cluster);
                    Optional<Component> componentOptional = componentRepository.findByClusterClusterNameAndComponentName(clusterName, componentName);
                    if (componentOptional.isPresent()) {
                        component.setId(componentOptional.get().getId());
                    }
                    component = componentRepository.save(component);

                    // 3. Persist hostComponent
                    Set<String> hostSet = componentHostMapping.get(componentName);
                    List<Host> hostList = hostRepository.findAllByHostnameIn(hostSet);
                    for (Host host : hostList) {
                        HostComponent hostComponent = new HostComponent();
                        hostComponent.setHost(host);
                        hostComponent.setComponent(component);
                        hostComponent.setStatus(true);

                        Optional<HostComponent> hostComponentOptional = hostComponentRepository.findByComponentComponentNameAndHostHostname(componentName, host.getHostname());
                        hostComponentOptional.ifPresent(value -> hostComponent.setId(value.getId()));
                        hostComponentRepository.save(hostComponent);
                    }
                }
            }
        }
    }
}
