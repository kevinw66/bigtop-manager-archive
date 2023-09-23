package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.CommandType;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.req.command.AbstractCommandReq;
import org.apache.bigtop.manager.server.model.req.command.ClusterCommandReq;
import org.apache.bigtop.manager.server.model.req.command.ComponentCommandReq;
import org.apache.bigtop.manager.server.model.req.command.HostCommandReq;
import org.apache.bigtop.manager.server.model.req.command.ServiceCommandReq;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommandMapper {
    CommandMapper INSTANCE = Mappers.getMapper(CommandMapper.class);

    @Mapping(target = "commandType", expression = "java(command2Type(commandReq))")
    CommandDTO Req2DTO(ClusterCommandReq commandReq);

    @Mapping(target = "commandType", expression = "java(command2Type(commandReq))")
    CommandDTO Req2DTO(ServiceCommandReq commandReq);

    @Mapping(target = "commandType", expression = "java(command2Type(commandReq))")
    CommandDTO Req2DTO(ComponentCommandReq commandReq);

    @Mapping(target = "commandType", expression = "java(command2Type(commandReq))")
    CommandDTO Req2DTO(HostCommandReq commandReq);

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "command", source = "commandDTO.command")
    CommandEvent DTO2Event(CommandDTO commandDTO, Job job);

    default CommandType command2Type(AbstractCommandReq commandReq) {
        CommandType commandType = null;

        if (commandReq instanceof ServiceCommandReq) {
            if (commandReq.getCommand() == Command.INSTALL) {
                commandType = CommandType.INSTALL_SERVICE;
            } else {
                commandType = CommandType.SERVICE;
            }
        } else if (commandReq instanceof ComponentCommandReq) {
            commandType = CommandType.COMPONENT;
        } else if (commandReq instanceof ClusterCommandReq) {
            commandType = CommandType.CLUSTER;
        } else if (commandReq instanceof HostCommandReq) {
            commandType = CommandType.HOST;
        }

        return commandType;
    }

}
