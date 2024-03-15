package org.apache.bigtop.manager.server.command.job.runner.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.enums.MaintainState;
import org.apache.bigtop.manager.dao.entity.*;
import org.apache.bigtop.manager.dao.repository.*;
import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.server.command.job.runner.AbstractJobRunner;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.model.dto.ComponentHostDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.service.ConfigService;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceInstallJobRunner extends AbstractJobRunner {

    @Resource
    private ConfigService configService;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostRepository hostRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return new CommandIdentifier(CommandLevel.SERVICE, Command.INSTALL);
    }

    @Override
    public void beforeRun() {
        super.beforeRun();

        CommandDTO commandDTO = getCommandDTO();
        List<ServiceCommandDTO> serviceCommands = commandDTO.getServiceCommands();
        Long clusterId = commandDTO.getClusterId();

        // Persist service, component and hostComponent metadata to database
        for (ServiceCommandDTO serviceCommand : serviceCommands) {
            String serviceName = serviceCommand.getServiceName();
            Service service = serviceRepository.findByClusterIdAndServiceName(clusterId, serviceName);
            upsertService(service, serviceCommand);
        }
    }

    private void upsertService(Service service, ServiceCommandDTO serviceCommand) {
        CommandDTO commandDTO = getCommandDTO();
        Long clusterId = commandDTO.getClusterId();
        String serviceName = serviceCommand.getServiceName();
        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        String stackName = cluster.getStack().getStackName();
        String stackVersion = cluster.getStack().getStackVersion();

        // 1. Persist service
        if (service == null) {
            ServiceDTO serviceDTO = StackUtils.getServiceDTO(stackName, stackVersion, serviceName);
            service = ServiceMapper.INSTANCE.fromDTO2Entity(serviceDTO, cluster);
            service = serviceRepository.save(service);
        }

        // 2. Update configs
        configService.upsert(clusterId, service.getId(), serviceCommand.getConfigs());

        for (ComponentHostDTO componentHostDTO : serviceCommand.getComponentHosts()) {
            String componentName = componentHostDTO.getComponentName();

            // 3. Persist component
            Component component = componentRepository.findByClusterIdAndComponentName(clusterId, componentName);
            if (component == null) {
                ComponentDTO componentDTO = StackUtils.getComponentDTO(stackName, stackVersion, componentName);
                component = ComponentMapper.INSTANCE.fromDTO2Entity(componentDTO, service, cluster);
                component = componentRepository.save(component);
            }

            // 4. Persist hostComponent
            for (String hostname : componentHostDTO.getHostnames()) {
                HostComponent hostComponent = hostComponentRepository.findByComponentComponentNameAndHostHostname(componentName, hostname);
                if (hostComponent == null) {
                    Host host = hostRepository.findByHostname(hostname);

                    hostComponent = new HostComponent();
                    hostComponent.setHost(host);
                    hostComponent.setComponent(component);
                    hostComponent.setState(MaintainState.UNINSTALLED);
                    hostComponentRepository.save(hostComponent);
                }
            }
        }
    }
}
