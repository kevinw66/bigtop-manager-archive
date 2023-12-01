package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.vo.ComponentVO;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.repository.ComponentRepository;
import org.apache.bigtop.manager.server.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private ComponentRepository componentRepository;

    @Override
    public List<ComponentVO> list(Long clusterId) {
        List<ComponentVO> componentVOList = new ArrayList<>();
        componentRepository.findAllByClusterId(clusterId).forEach(component -> {
            ComponentVO componentVO = ComponentMapper.INSTANCE.fromEntity2VO(component);
            componentVOList.add(componentVO);
        });

        return componentVOList;
    }

    @Override
    public ComponentVO get(Long id) {
        Component component = componentRepository.findById(id).orElseThrow(() -> new ApiException(ApiExceptionEnum.COMPONENT_NOT_FOUND));
        return ComponentMapper.INSTANCE.fromEntity2VO(component);
    }

}
