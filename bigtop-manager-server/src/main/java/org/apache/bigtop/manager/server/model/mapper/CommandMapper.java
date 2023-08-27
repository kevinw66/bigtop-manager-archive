package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.request.CommandRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommandMapper {
    CommandMapper INSTANCE = Mappers.getMapper(CommandMapper.class);

    CommandDTO Request2DTO(CommandRequest commandRequest);

}
