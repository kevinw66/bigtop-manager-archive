package org.apache.bigtop.manager.server.validate;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClusterCreateValidator {

    @Resource
    private ClusterRepository clusterRepository;

    public void validate(String clusterName) {
        Cluster cluster = clusterRepository.findByClusterNameAndStateIn(clusterName, List.of(MaintainState.INSTALLED, MaintainState.MAINTAINED));

        // Check hosts
        if (cluster != null) {
            throw new ApiException(ApiExceptionEnum.CLUSTER_IS_INSTALLED, clusterName);
        }
    }
}
