package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.ComponentCategories;
import org.apache.bigtop.manager.common.enums.MaintainState;
import org.apache.bigtop.manager.dao.entity.HostComponent;
import org.apache.bigtop.manager.dao.entity.Service;
import org.apache.bigtop.manager.dao.repository.HostComponentRepository;
import org.apache.bigtop.manager.dao.repository.ServiceRepository;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.vo.ServiceVO;
import org.apache.bigtop.manager.server.service.ServiceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Override
    public List<ServiceVO> list(Long clusterId) {
        List<ServiceVO> res = new ArrayList<>();
        List<HostComponent> hostComponentList = hostComponentRepository.findAllByComponentClusterId(clusterId);
        Map<Long, List<HostComponent>> serviceIdToHostComponent = hostComponentList
                .stream()
                .collect(Collectors.groupingBy(hostComponent -> hostComponent.getComponent().getService().getId()));

        for (Map.Entry<Long, List<HostComponent>> entry : serviceIdToHostComponent.entrySet()) {
            List<HostComponent> hostComponents = entry.getValue();
            Service service = hostComponents.get(0).getComponent().getService();
            ServiceVO serviceVO = ServiceMapper.INSTANCE.fromEntity2VO(service);

            boolean isHealthy = true;
            boolean isClient = true;
            for (HostComponent hostComponent : hostComponents) {
                String category = hostComponent.getComponent().getCategory();
                if (!category.equalsIgnoreCase(ComponentCategories.CLIENT)) {
                    isClient = false;
                }

                MaintainState expectedState = category.equalsIgnoreCase(ComponentCategories.CLIENT) ? MaintainState.INSTALLED : MaintainState.STARTED;
                if (!hostComponent.getState().equals(expectedState)) {
                    isHealthy = false;
                }
            }

            serviceVO.setIsClient(isClient);
            serviceVO.setIsHealthy(isHealthy);
            res.add(serviceVO);
        }

        return res;
    }

    @Override
    public ServiceVO get(Long id) {
        Service service = serviceRepository.findById(id).orElse(new Service());
        return ServiceMapper.INSTANCE.fromEntity2VO(service);
    }
}
