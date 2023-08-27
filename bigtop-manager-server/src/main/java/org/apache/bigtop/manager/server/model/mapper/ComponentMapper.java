package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.Service;
import org.apache.bigtop.manager.server.stack.pojo.ComponentModel;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ComponentMapper {

    ComponentMapper INSTANCE = Mappers.getMapper(ComponentMapper.class);

    @Mapping(target = "scriptId", source = "componentDTO.commandScript.scriptId")
    Component DTO2Entity(ComponentDTO componentDTO);

    @Mapping(target = "scriptId", source = "componentDTO.commandScript.scriptId")
    @Mapping(target = "service", expression = "java(service)")
    @Mapping(target = "cluster", expression = "java(cluster)")
    Component DTO2Entity(ComponentDTO componentDTO, @Context Service service, @Context Cluster cluster);


    @Mapping(target = "componentName", source = "name")
    ComponentDTO Model2DTO(ComponentModel componentModel);

}
