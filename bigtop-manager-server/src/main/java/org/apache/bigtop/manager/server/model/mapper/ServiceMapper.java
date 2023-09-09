package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.dto.OSSpecificDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.vo.ServiceVO;
import org.apache.bigtop.manager.server.model.vo.ServiceVersionVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Service;
import org.apache.bigtop.manager.server.stack.pojo.ServiceModel;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {ComponentMapper.class})
public interface ServiceMapper {

    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    @Mapping(target = "osSpecifics", expression = "java(osSpecificDTO2str(serviceDTO.getOsSpecifics()))")
    @Mapping(target = "cluster", expression = "java(cluster)")
    Service DTO2Entity(ServiceDTO serviceDTO, @Context Cluster cluster);

    ServiceVersionVO DTO2VO(ServiceDTO serviceDTO);

    @Mapping(target = "serviceName", source = "name")
    @Mapping(target = "serviceDesc", source = "desc")
    @Mapping(target = "serviceVersion", source = "version")
    @Mapping(target = "serviceUser", source = "user")
    @Mapping(target = "serviceGroup", source = "group")
    ServiceDTO Model2DTO(ServiceModel serviceModel);

    @Mapping(target = "clusterName", source = "cluster.clusterName")
    ServiceVO Entity2VO(Service service);

    default String osSpecificDTO2str(List<OSSpecificDTO> osSpecifics) {
        return JsonUtils.writeAsString(osSpecifics);
    }
}
