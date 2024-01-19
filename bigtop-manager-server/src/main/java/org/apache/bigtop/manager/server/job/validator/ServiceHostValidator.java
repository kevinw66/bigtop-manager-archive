package org.apache.bigtop.manager.server.job.validator;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.command.ServiceCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ServiceHostValidator implements CommandValidator {

    @Resource
    private HostRepository hostRepository;

    @Override
    public List<CommandIdentifier> getCommandIdentifiers() {
        return List.of(new CommandIdentifier(CommandLevel.SERVICE, Command.INSTALL));
    }

    @Override
    public void validate(ValidatorContext context) {
        CommandDTO commandDTO = context.getCommandDTO();
        List<ServiceCommandDTO> serviceCommands = commandDTO.getServiceCommands();

        Set<String> hostnameSet = serviceCommands.stream()
                .flatMap(x -> x.getComponentHosts().stream())
                .flatMap(x -> x.getHostnames().stream())
                .collect(Collectors.toSet());

        List<Host> hostnames = hostRepository.findAllByHostnameIn(hostnameSet);

        if (hostnames.size() != hostnameSet.size()) {
            throw new ApiException(ApiExceptionEnum.HOST_NOT_FOUND);
        }
    }

}
