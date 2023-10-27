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

    StackDTO Req2DTO(StackReq stackReq);

    Stack DTO2Entity(StackDTO stackDTO);

    StackVO Entity2VO(Stack stack);

    StackDTO Entity2DTO(Stack stack);

    StackVO DTO2VO(StackDTO stackDTO);

    StackDTO Model2DTO(StackModel stackModel);

}
