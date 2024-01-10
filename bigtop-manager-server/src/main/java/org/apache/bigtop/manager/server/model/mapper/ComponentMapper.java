package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.model.vo.ComponentVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.Service;
import org.apache.bigtop.manager.server.stack.pojo.ComponentModel;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {TypeConvert.class})
public interface ComponentMapper {

    ComponentMapper INSTANCE = Mappers.getMapper(ComponentMapper.class);

    @Mapping(target = "commandScript", source = "commandScript", qualifiedByName = "obj2Json")
    @Mapping(target = "customCommands", source = "customCommands", qualifiedByName = "obj2Json")
    @Mapping(target = "service", expression = "java(service)")
    @Mapping(target = "cluster", expression = "java(cluster)")
    Component fromDTO2Entity(ComponentDTO componentDTO, @Context Service service, @Context Cluster cluster);

    ComponentVO fromDTO2VO(ComponentDTO componentDTO);

    List<ComponentVO> fromDTO2VO(List<ComponentDTO> componentDTOList);

    @Mapping(target = "componentName", source = "name")
    ComponentDTO fromModel2DTO(ComponentModel componentModel);

    @Mapping(target = "serviceName", source = "service.serviceName")
    @Mapping(target = "clusterName", source = "cluster.clusterName")
    ComponentVO fromEntity2VO(Component component);

    List<ComponentVO> fromEntity2VO(List<Component> components);

}
