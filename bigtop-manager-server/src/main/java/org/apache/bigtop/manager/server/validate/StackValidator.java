package org.apache.bigtop.manager.server.validate;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.ValidateType;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.springframework.stereotype.Component;

@Component
public class StackValidator extends AbstractChainValidator {

    @Resource
    private StackRepository stackRepository;

    @Override
    public void setValidateType() {
        this.validateType = ValidateType.CLUSTER_ADD;
    }

    @Override
    public void vaildate(ChainContext context) {
        ClusterDTO clusterDTO = context.getClusterDTO();
        String stackName = clusterDTO.getStackName();
        String stackVersion = clusterDTO.getStackVersion();

        Stack stack = stackRepository.findByStackNameAndStackVersion(stackName, stackVersion);
        if (stack == null) {
            throw new ApiException(ApiExceptionEnum.STACK_NOT_FOUND);
        }
    }

}
