package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.vo.StageVO;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StageMapper {

    StageMapper INSTANCE = Mappers.getMapper(StageMapper.class);

    StageVO Entity2VO(Stage stage);
}
