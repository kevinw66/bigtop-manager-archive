package org.apache.bigtop.manager.server.job.validator;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.model.dto.command.ClusterCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StackValidator implements CommandValidator {

    @Resource
    private StackRepository stackRepository;

    @Override
    public List<CommandIdentifier> getCommandIdentifiers() {
        return List.of(new CommandIdentifier(CommandLevel.CLUSTER, Command.INSTALL));
    }

    @Override
    public void validate(ValidatorContext context) {
        ClusterCommandDTO clusterCommand = context.getCommandDTO().getClusterCommand();
        String stackName = clusterCommand.getStackName();
        String stackVersion = clusterCommand.getStackVersion();

        Stack stack = stackRepository.findByStackNameAndStackVersion(stackName, stackVersion);
        if (stack == null) {
            throw new ApiException(ApiExceptionEnum.STACK_NOT_FOUND);
        }
    }

}
