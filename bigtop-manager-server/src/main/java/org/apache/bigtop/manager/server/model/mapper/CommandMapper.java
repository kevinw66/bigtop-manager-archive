package org.apache.bigtop.manager.server.model.mapper;

import org.apache.bigtop.manager.server.enums.CommandEvent;
import org.apache.bigtop.manager.server.enums.CommandType;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.request.command.AbstractCommandRequest;
import org.apache.bigtop.manager.server.model.request.command.ClusterCommandRequest;
import org.apache.bigtop.manager.server.model.request.command.ComponentCommandRequest;
import org.apache.bigtop.manager.server.model.request.command.HostCommandRequest;
import org.apache.bigtop.manager.server.model.request.command.ServiceCommandRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommandMapper {
    CommandMapper INSTANCE = Mappers.getMapper(CommandMapper.class);

    @Mapping(target = "commandType", expression = "java(command2Type(commandRequest))")
    CommandDTO Request2DTO(ClusterCommandRequest commandRequest);

    @Mapping(target = "commandType", expression = "java(command2Type(commandRequest))")
    CommandDTO Request2DTO(ServiceCommandRequest commandRequest);

    @Mapping(target = "commandType", expression = "java(command2Type(commandRequest))")
    CommandDTO Request2DTO(ComponentCommandRequest commandRequest);

    @Mapping(target = "commandType", expression = "java(command2Type(commandRequest))")
    CommandDTO Request2DTO(HostCommandRequest commandRequest);


    default CommandType command2Type(AbstractCommandRequest commandRequest) {
        CommandType commandType = null;

        if (commandRequest instanceof ServiceCommandRequest) {
            if (commandRequest.getCommand().equals(CommandEvent.INSTALL.name())) {
                commandType = CommandType.INSTALL_SERVICE;
            } else {
                commandType = CommandType.SERVICE;
            }
        } else if (commandRequest instanceof ComponentCommandRequest) {
            commandType = CommandType.COMPONENT;
        } else if (commandRequest instanceof ClusterCommandRequest) {
            commandType = CommandType.CLUSTER;
        } else if (commandRequest instanceof HostCommandRequest) {
            commandType = CommandType.HOST;
        }

        return commandType;
    }

}
