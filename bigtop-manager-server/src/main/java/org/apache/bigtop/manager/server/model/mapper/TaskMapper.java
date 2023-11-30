package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.vo.TaskVO;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskVO fromEntity2VO(Task task);
}
