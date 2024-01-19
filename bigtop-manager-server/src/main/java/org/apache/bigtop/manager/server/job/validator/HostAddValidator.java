package org.apache.bigtop.manager.server.job.validator;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.model.dto.command.HostCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HostAddValidator implements CommandValidator {

    @Resource
    private HostRepository hostRepository;

    @Override
    public List<CommandIdentifier> getCommandIdentifiers() {
        return List.of(new CommandIdentifier(CommandLevel.HOST, Command.INSTALL));
    }

    @Override
    public void validate(ValidatorContext context) {
        List<String> hostnames = context.getCommandDTO().getHostCommands().stream().map(HostCommandDTO::getHostname).toList();

        List<Host> hosts = hostRepository.findAllByHostnameIn(hostnames);
        if (CollectionUtils.isNotEmpty(hosts)) {
            List<String> existsHostnames = hosts.stream().map(Host::getHostname).toList();
            throw new ApiException(ApiExceptionEnum.HOST_ASSIGNED, String.join(",", existsHostnames));
        }
    }

}
