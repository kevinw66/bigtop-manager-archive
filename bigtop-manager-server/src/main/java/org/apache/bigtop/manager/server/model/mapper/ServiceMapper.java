package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.vo.ServiceVersionVO;
import org.apache.bigtop.manager.server.stack.pojo.ServiceModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceMapper {

    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    ServiceVersionVO DTO2VO(ServiceDTO serviceDTO);

    @Mapping(target = "serviceName", source = "name")
    @Mapping(target = "serviceDesc", source = "desc")
    @Mapping(target = "serviceVersion", source = "version")
    @Mapping(target = "serviceUser", source = "user")
    @Mapping(target = "serviceGroup", source = "group")
    ServiceDTO Model2DTO(ServiceModel serviceModel);

}
