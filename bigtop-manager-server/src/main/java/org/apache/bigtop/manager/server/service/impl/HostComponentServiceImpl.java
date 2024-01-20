package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.ComponentCommandDTO;
import org.apache.bigtop.manager.server.model.mapper.HostComponentMapper;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.repository.ComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.bigtop.manager.server.service.HostComponentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class HostComponentServiceImpl implements HostComponentService {

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostRepository hostRepository;

    @Override
    public List<HostComponentVO> list(Long clusterId) {
        List<HostComponent> hostComponentList = hostComponentRepository.findAllByComponentClusterId(clusterId);
        return HostComponentMapper.INSTANCE.fromEntity2VO(hostComponentList);
    }

    @Override
    public List<HostComponentVO> listByHost(Long clusterId, Long hostId) {
        List<HostComponent> hostComponentList = hostComponentRepository.findAllByComponentClusterIdAndHostId(clusterId, clusterId);
        return HostComponentMapper.INSTANCE.fromEntity2VO(hostComponentList);
    }

    @Override
    public List<HostComponentVO> listByService(Long clusterId, Long serviceId) {
        List<HostComponent> hostComponentList = hostComponentRepository.findAllByComponentClusterIdAndComponentServiceId(clusterId, serviceId);
        return HostComponentMapper.INSTANCE.fromEntity2VO(hostComponentList);
    }

    @Override
    public void batchSave(Long clusterId, List<ComponentCommandDTO> componentCommandDTOs) {
        // Persist hostComponent to database
        for (ComponentCommandDTO componentCommandDTO : componentCommandDTOs) {
            String componentName = componentCommandDTO.getComponentName();
            List<String> hostnames = componentCommandDTO.getHostnames();

            Component component = componentRepository.findByClusterIdAndComponentName(clusterId, componentName).orElse(new Component());
            List<Host> hosts = hostRepository.findAllByHostnameIn(hostnames);
            for (Host host : hosts) {
                HostComponent hostComponent = new HostComponent();
                hostComponent.setHost(host);
                hostComponent.setComponent(component);
                hostComponent.setState(MaintainState.INSTALLED);

                Optional<HostComponent> hostComponentOptional = hostComponentRepository.findByComponentComponentNameAndHostHostname(component.getComponentName(), host.getHostname());
                hostComponentOptional.ifPresent(value -> hostComponent.setId(value.getId()));
                hostComponentRepository.save(hostComponent);
            }
        }
    }

    @Override
    public void saveByCommand(CommandDTO commandDTO) {
        Long clusterId = commandDTO.getClusterId();
        List<ComponentCommandDTO> componentCommands = commandDTO.getComponentCommands();

        // Persist hostComponent to database
        batchSave(clusterId, componentCommands);
    }
}
