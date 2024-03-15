package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.dao.entity.ServiceConfig;
import org.apache.bigtop.manager.server.model.vo.ServiceConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {TypeConfigMapper.class})
public interface ServiceConfigMapper {

    ServiceConfigMapper INSTANCE = Mappers.getMapper(ServiceConfigMapper.class);

    @Mapping(target = "serviceName", source = "service.serviceName")
    @Mapping(target = "configs", source = "configs", qualifiedByName = "fromEntity2VO")
    ServiceConfigVO fromEntity2VO(ServiceConfig serviceConfig);

    List<ServiceConfigVO> fromEntity2VO(List<ServiceConfig> serviceConfigs);
}
