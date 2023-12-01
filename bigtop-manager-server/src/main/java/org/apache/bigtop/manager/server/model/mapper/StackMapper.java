package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.req.StackReq;
import org.apache.bigtop.manager.server.model.vo.StackVO;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.stack.pojo.StackModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StackMapper {

    StackMapper INSTANCE = Mappers.getMapper(StackMapper.class);

    StackVO fromEntity2VO(Stack stack);

    StackVO fromDTO2VO(StackDTO stackDTO);

    StackDTO fromModel2DTO(StackModel stackModel);

}
