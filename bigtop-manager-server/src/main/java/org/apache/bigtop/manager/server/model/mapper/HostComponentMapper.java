package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.dao.entity.HostComponent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HostComponentMapper {

    HostComponentMapper INSTANCE = Mappers.getMapper(HostComponentMapper.class);

    @Mapping(target = "componentName", source = "component.componentName")
    @Mapping(target = "displayName", source = "component.displayName")
    @Mapping(target = "category", source = "component.category")
    @Mapping(target = "serviceName", source = "component.service.serviceName")
    @Mapping(target = "clusterName", source = "component.cluster.clusterName")
    @Mapping(target = "hostname", source = "host.hostname")
    HostComponentVO fromEntity2VO(HostComponent hostComponent);

    List<HostComponentVO> fromEntity2VO(List<HostComponent> hostComponents);

}
