package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.vo.StageVO;
import org.apache.bigtop.manager.dao.entity.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {TaskMapper.class})
public interface StageMapper {

    StageMapper INSTANCE = Mappers.getMapper(StageMapper.class);

    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    StageVO fromEntity2VO(Stage stage);
}
