package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.HostComponentMapper;
import org.apache.bigtop.manager.server.model.vo.ComponentVO;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.repository.ComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Override
    public List<ComponentVO> list() {
        List<ComponentVO> componentVOList = new ArrayList<>();
        componentRepository.findAll().forEach(stack -> {
            ComponentVO componentVO = ComponentMapper.INSTANCE.Entity2VO(stack);
            componentVOList.add(componentVO);
        });

        return componentVOList;
    }

    @Override
    public ComponentVO get(Long id) {
        Component component = componentRepository.findById(id).orElseThrow(() -> new ApiException(ApiExceptionEnum.COMPONENT_NOT_FOUND));
        return ComponentMapper.INSTANCE.Entity2VO(component);
    }

    @Override
    public List<HostComponentVO> hostComponent(Long id) {
        List<HostComponent> hostComponentList = hostComponentRepository.findAllByComponentId(id);
        return HostComponentMapper.INSTANCE.Entity2VO(hostComponentList);
    }

}
