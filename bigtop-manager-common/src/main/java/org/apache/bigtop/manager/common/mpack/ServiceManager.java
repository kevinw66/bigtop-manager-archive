package org.apache.bigtop.manager.common.mpack;

import org.apache.bigtop.manager.common.mpack.info.ServiceInfo;
import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

/**
 * Execute when init cluster
 * Then get all Script Object for all services
 */
@Component
public class ServiceManager {

    @Resource
    private YamlUtils<ServiceInfo> yamlUtils;

    public ServiceInfo initConfig(String path) throws FileNotFoundException {
        return yamlUtils.loadYaml(path, ServiceInfo.class);
    }

}