package org.apache.bigtop.manager.mpack;

import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.apache.bigtop.manager.mpack.info.ServiceInfo;
import org.apache.bigtop.manager.spi.mpack.Script;
import org.apache.bigtop.manager.spi.plugin.SPIFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Execute when init cluster
 * Then get all Script Object for all services
 */
@Component
public class ServiceManager {

    public Map<String, Script> scriptMap;

    public void initScripts() {
        SPIFactory<Script> spiFactory = new SPIFactory<>(Script.class);
        scriptMap = spiFactory.getSPIMap();
    }

    @Resource
    private YamlUtils<ServiceInfo> yamlUtils;

    public ServiceInfo serviceInfo;

    public void initConfig(String path) throws FileNotFoundException {
        serviceInfo = yamlUtils.loadYaml(path, ServiceInfo.class);
    }

}