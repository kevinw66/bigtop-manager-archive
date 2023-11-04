package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.req.CommandReq;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommandMapper {
    CommandMapper INSTANCE = Mappers.getMapper(CommandMapper.class);

    CommandDTO Req2DTO(CommandReq commandReq);

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "command", source = "commandDTO.command")
    CommandEvent DTO2Event(CommandDTO commandDTO, Job job);

}
