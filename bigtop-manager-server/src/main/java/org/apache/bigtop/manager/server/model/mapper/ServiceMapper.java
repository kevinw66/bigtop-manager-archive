package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.dto.OSSpecificDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.vo.ServiceVO;
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
    Service fromDTO2Entity(ServiceDTO serviceDTO, @Context Cluster cluster);

    ServiceVO fromDTO2VO(ServiceDTO serviceDTO);

    List<ServiceVO> fromDTO2VO(List<ServiceDTO> serviceDTOList);

    @Mapping(target = "serviceName", source = "name")
    @Mapping(target = "serviceDesc", source = "desc")
    @Mapping(target = "serviceVersion", source = "version")
    @Mapping(target = "serviceUser", source = "user")
    @Mapping(target = "serviceGroup", source = "group")
    ServiceDTO fromModel2DTO(ServiceModel serviceModel);

    @Mapping(target = "clusterName", source = "cluster.clusterName")
    ServiceVO fromEntity2VO(Service service);

    List<ServiceVO> fromEntity2VO(List<Service> services);

    default String osSpecificDTO2str(List<OSSpecificDTO> osSpecifics) {
        return JsonUtils.writeAsString(osSpecifics);
    }
}
