package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
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
    public void batchSave(Long clusterId, String hostname, List<String> componentNames) {
        // Persist hostComponent to database
        List<Component> componentList = componentRepository.findAllByClusterIdAndComponentNameIn(clusterId, componentNames);
        Host host = hostRepository.findByHostname(hostname);
        for (Component component : componentList) {
            HostComponent hostComponent = new HostComponent();
            hostComponent.setHost(host);
            hostComponent.setComponent(component);
            hostComponent.setState(MaintainState.INSTALLED);

            Optional<HostComponent> hostComponentOptional = hostComponentRepository.findByComponentComponentNameAndHostHostname(component.getComponentName(), host.getHostname());
            hostComponentOptional.ifPresent(value -> hostComponent.setId(value.getId()));
            hostComponentRepository.save(hostComponent);
        }
    }

    @Override
    public void saveByCommand(CommandDTO commandDTO) {
        List<String> componentNameList = commandDTO.getHostCommand().getComponentNames();
        String hostname = commandDTO.getHostCommand().getHostname();
        Long clusterId = commandDTO.getClusterId();

        // Persist hostComponent to database
        batchSave(clusterId, hostname, componentNameList);
    }
}
