package org.apache.bigtop.manager.server.validate;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.ValidateType;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.command.ClusterCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.springframework.stereotype.Component;

@Component
public class StackValidator implements ChainValidator {

    @Resource
    private StackRepository stackRepository;

    @Override
    public ValidateType getValidateType() {
        return ValidateType.CLUSTER_INSTALL;
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
