package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.mapper.HostComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.ServiceVO;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.entity.Service;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.ServiceRepository;
import org.apache.bigtop.manager.server.service.ServiceService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

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

}
