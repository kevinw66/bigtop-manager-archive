package org.apache.bigtop.manager.server.validate;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClusterCreateValidator {

    @Resource
    private ClusterRepository clusterRepository;

    public void validate(String clusterName) {
        Optional<Cluster> clusterOptional = clusterRepository.findByClusterName(clusterName);

        // Check hosts
        if (clusterOptional.isPresent()) {
            throw new ApiException(ApiExceptionEnum.CLUSTER_IS_INSTALLED, clusterName);
        }
    }
}
