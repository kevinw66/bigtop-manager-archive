package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.model.dto.ComponentDTO;
import org.apache.bigtop.manager.server.model.dto.CustomCommandDTO;
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

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ComponentMapper {

    ComponentMapper INSTANCE = Mappers.getMapper(ComponentMapper.class);

    @Mapping(target = "commandScript", expression = "java(commandScript2str(componentDTO.getCommandScript()))")
    @Mapping(target = "customCommands", expression = "java(customCommands2str(componentDTO.getCustomCommands()))")
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

    default List<ComponentVO> fromEntity2VO(List<Component> components) {
        if (components == null) {
            return null;
        }

        List<ComponentVO> list = new ArrayList<>(components.size());
        for (Component component : components) {
            list.add(fromEntity2VO(component));
        }

        return list;
    }

    default String commandScript2str(ScriptDTO commandScript) {
        return JsonUtils.writeAsString(commandScript);
    }

    default String customCommands2str(List<CustomCommandDTO> customCommands) {
        return JsonUtils.writeAsString(customCommands);
    }

}
