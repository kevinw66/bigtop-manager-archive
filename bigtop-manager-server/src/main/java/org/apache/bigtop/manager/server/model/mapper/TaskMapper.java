package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.vo.TaskVO;
import org.apache.bigtop.manager.dao.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    TaskVO fromEntity2VO(Task task);
}
