package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.dao.entity.TypeConfig;
import org.apache.bigtop.manager.server.model.dto.TypeConfigDTO;
import org.apache.bigtop.manager.server.model.vo.TypeConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {TypeConvert.class})
public interface TypeConfigMapper {

    TypeConfigMapper INSTANCE = Mappers.getMapper(TypeConfigMapper.class);

    @Mapping(target = "properties", source = "propertiesJson", qualifiedByName = "json2PropertyDTOList")
    TypeConfigDTO fromEntity2DTO(TypeConfig typeConfig);

    List<TypeConfigDTO> fromEntity2DTO(List<TypeConfig> typeConfigs);

    TypeConfigVO fromDTO2VO(TypeConfigDTO typeConfigDTO);

    List<TypeConfigVO> fromDTO2VO(List<TypeConfigDTO> typeConfigDTOList);

    @Mapping(target = "properties", source = "propertiesJson", qualifiedByName = "json2PropertyVOList")
    TypeConfigVO fromEntity2VO(TypeConfig typeConfig);

    @Named("fromEntity2VO")
    List<TypeConfigVO> fromEntity2VO(List<TypeConfig> typeConfigs);
}
