package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.pojo.stack.ServiceInfo;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.request.StackRequest;
import org.apache.bigtop.manager.server.model.vo.ServiceVersionVO;
import org.apache.bigtop.manager.server.model.vo.StackVO;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceMapper {

    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    ServiceVersionVO POJO2VO(ServiceInfo serviceInfo);

}
