package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.model.req.CommandReq;
import org.apache.bigtop.manager.server.model.req.command.ServiceCommandReq;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CommandMapper {
    CommandMapper INSTANCE = Mappers.getMapper(CommandMapper.class);

    CommandDTO fromReq2DTO(CommandReq commandReq);

    ServiceCommandDTO fromServiceReq2DTO(ServiceCommandReq req);

    List<ServiceCommandDTO> fromServiceReq2DTO(List<ServiceCommandReq> reqs);

}
