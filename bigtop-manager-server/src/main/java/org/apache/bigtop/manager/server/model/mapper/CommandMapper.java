package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.enums.CommandEvent;
import org.apache.bigtop.manager.server.enums.CommandType;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.req.command.AbstractCommandReq;
import org.apache.bigtop.manager.server.model.req.command.ClusterCommandReq;
import org.apache.bigtop.manager.server.model.req.command.ComponentCommandReq;
import org.apache.bigtop.manager.server.model.req.command.HostCommandReq;
import org.apache.bigtop.manager.server.model.req.command.ServiceCommandReq;
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


    default CommandType command2Type(AbstractCommandReq commandReq) {
        CommandType commandType = null;

        if (commandReq instanceof ServiceCommandReq) {
            if (commandReq.getCommand().equals(CommandEvent.INSTALL.name())) {
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
