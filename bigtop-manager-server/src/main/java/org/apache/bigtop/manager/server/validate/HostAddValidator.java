package org.apache.bigtop.manager.server.validate;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.ValidateType;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HostAddValidator implements ChainValidator {

    @Resource
    private HostRepository hostRepository;

    @Override
    public ValidateType getValidateType() {
        return ValidateType.HOST_INSTALL;
    }

    @Override
    public void validate(ValidatorContext context) {
        List<String> hostnames = context.getHostnames();

        List<Host> hosts = hostRepository.findAllByHostnameIn(hostnames);
        if (CollectionUtils.isNotEmpty(hosts)) {
            List<String> existsHostnames = hosts.stream().map(Host::getHostname).toList();
            throw new ApiException(ApiExceptionEnum.HOST_ASSIGNED, String.join(",", existsHostnames));
        }
    }

}
