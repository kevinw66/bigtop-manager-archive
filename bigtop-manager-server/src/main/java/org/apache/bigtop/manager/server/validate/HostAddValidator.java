package org.apache.bigtop.manager.server.validate;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.MaintainState;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HostAddValidator {

    @Resource
    private HostRepository hostRepository;

    public void validate(List<String> hostnames) {
        // Check hosts
        List<Host> hosts = hostRepository.findAllByHostnameInAndStateIn(hostnames, List.of(MaintainState.INSTALLED, MaintainState.MAINTAINED));
        if (CollectionUtils.isNotEmpty(hosts)) {
            List<String> existsHostnames = hosts.stream().map(Host::getHostname).toList();
            throw new ApiException(ApiExceptionEnum.HOST_ASSIGNED, String.join(",", existsHostnames));
        }
    }
}
