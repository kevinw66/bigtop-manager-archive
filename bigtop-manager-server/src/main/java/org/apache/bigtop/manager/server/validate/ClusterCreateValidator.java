package org.apache.bigtop.manager.server.validate;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.ValidateType;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.command.ClusterCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClusterCreateValidator implements ChainValidator {

    @Resource
    private ClusterRepository clusterRepository;

    @Override
    public ValidateType getValidateType() {
        return ValidateType.CLUSTER_INSTALL;
    }

    @Override
    public void validate(ValidatorContext context) {
        ClusterCommandDTO clusterCommand = context.getCommandDTO().getClusterCommand();
        String clusterName = clusterCommand.getClusterName();

        Optional<Cluster> clusterOptional = clusterRepository.findByClusterName(clusterName);

        if (clusterOptional.isPresent()) {
            throw new ApiException(ApiExceptionEnum.CLUSTER_IS_INSTALLED, clusterName);
        }
    }
}
