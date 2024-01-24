package org.apache.bigtop.manager.server.job.factory.service;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.job.factory.component.AbstractComponentJobFactory;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.repository.ComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Service Job can be seen as a collection of multiple Components and Hosts,
 * so it can directly inherit from AbstractComponentJobFactory.
 */
public abstract class AbstractServiceJobFactory extends AbstractComponentJobFactory {

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    protected Map<String, List<String>> getComponentHostMapping(Command command) {
        Map<String, List<String>> componentHostMapping = new HashMap<>();
        for (Component component : getComponents()) {
            String componentName = component.getComponentName();
            List<HostComponent> hostComponentList = hostComponentRepository
                    .findAllByComponentClusterIdAndComponentComponentName(cluster.getId(), componentName);

            List<String> hostnames = hostComponentList.stream().map(x -> x.getHost().getHostname()).toList();
            componentHostMapping.put(componentName, hostnames);
        }

        return componentHostMapping;
    }

    protected List<Component> getComponents() {
        Long clusterId = jobContext.getCommandDTO().getClusterId();
        List<String> serviceNameList = jobContext.getCommandDTO().getServiceCommands()
                .stream()
                .map(ServiceCommandDTO::getServiceName)
                .toList();

        return componentRepository.findAllByClusterIdAndServiceServiceNameIn(clusterId, serviceNameList);
    }
}
