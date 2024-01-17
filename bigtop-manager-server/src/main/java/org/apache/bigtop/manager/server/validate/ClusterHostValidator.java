package org.apache.bigtop.manager.server.validate;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.ValidateType;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClusterHostValidator extends AbstractChainValidator {

    @Resource
    private HostRepository hostRepository;

    @Override
    public void setValidateType() {
        this.validateType = ValidateType.CLUSTER_ADD;
    }

    @Override
    public void vaildate(ChainContext context) {
        ClusterDTO clusterDTO = context.getClusterDTO();
        List<String> hostnames = clusterDTO.getHostnames();

        List<Host> hosts = hostRepository.findAllByHostnameIn(hostnames);
        if (CollectionUtils.isNotEmpty(hosts)) {
            List<String> existsHostnames = hosts.stream().map(Host::getHostname).toList();
            throw new ApiException(ApiExceptionEnum.HOST_ASSIGNED, String.join(",", existsHostnames));
        }
    }

}
