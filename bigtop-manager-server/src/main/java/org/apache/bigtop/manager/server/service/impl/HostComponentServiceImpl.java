package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.dao.entity.HostComponent;
import org.apache.bigtop.manager.dao.repository.ComponentRepository;
import org.apache.bigtop.manager.dao.repository.HostComponentRepository;
import org.apache.bigtop.manager.dao.repository.HostRepository;
import org.apache.bigtop.manager.server.model.mapper.HostComponentMapper;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.service.HostComponentService;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
