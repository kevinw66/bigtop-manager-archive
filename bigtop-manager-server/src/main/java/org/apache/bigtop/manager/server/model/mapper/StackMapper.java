package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.request.StackRequest;
import org.apache.bigtop.manager.server.model.vo.StackVO;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StackMapper {

    StackMapper INSTANCE = Mappers.getMapper(StackMapper.class);

    StackDTO Request2DTO(StackRequest stackRequest);

    Stack DTO2Entity(StackDTO stackDTO);

    StackVO Entity2VO(Stack stack);

}
