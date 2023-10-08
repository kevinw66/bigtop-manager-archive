package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.model.dto.ScriptDTO;
import org.apache.bigtop.manager.server.model.vo.ComponentVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.Service;
import org.apache.bigtop.manager.server.stack.pojo.ComponentModel;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface ComponentMapper {

    ComponentMapper INSTANCE = Mappers.getMapper(ComponentMapper.class);

    @Mapping(target = "commandScript", expression = "java(commandScript2str(componentDTO.getCommandScript()))")
    @Mapping(target = "customCommands", expression = "java(customCommands2str(componentDTO.getCustomCommands()))")
    @Mapping(target = "service", expression = "java(service)")
    @Mapping(target = "cluster", expression = "java(cluster)")
    Component DTO2Entity(ComponentDTO componentDTO, @Context Service service, @Context Cluster cluster);


    @Mapping(target = "componentName", source = "name")
    ComponentDTO Model2DTO(ComponentModel componentModel);

    @Mapping(target = "serviceName", source = "service.serviceName")
    @Mapping(target = "clusterName", source = "cluster.clusterName")
    ComponentVO Entity2VO(Component component);

    default String commandScript2str(ScriptDTO commandScript) {
        return JsonUtils.writeAsString(commandScript);
    }
    default String customCommands2str(Map<String, ScriptDTO> customCommands) {
        return JsonUtils.writeAsString(customCommands);
    }

}
