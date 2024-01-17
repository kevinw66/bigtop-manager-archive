package org.apache.bigtop.manager.server.validate;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.ValidateType;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClusterCreateValidator extends AbstractChainValidator {

    @Resource
    private ClusterRepository clusterRepository;

    @Override
    public void setValidateType() {
        this.validateType = ValidateType.CLUSTER_ADD;
    }

    @Override
    public void vaildate(ChainContext context) {
        ClusterDTO clusterDTO = context.getClusterDTO();
        String clusterName = clusterDTO.getClusterName();

        Optional<Cluster> clusterOptional = clusterRepository.findByClusterName(clusterName);

        if (clusterOptional.isPresent()) {
            throw new ApiException(ApiExceptionEnum.CLUSTER_IS_INSTALLED, clusterName);
        }
    }
}
