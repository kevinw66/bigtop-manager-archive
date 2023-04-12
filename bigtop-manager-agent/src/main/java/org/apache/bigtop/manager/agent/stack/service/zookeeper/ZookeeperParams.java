package org.apache.bigtop.manager.agent.stack.service.zookeeper;

import org.apache.bigtop.manager.agent.configuration.StackConfiguration;
import org.apache.bigtop.manager.agent.stack.StackParams;
import org.apache.bigtop.manager.agent.utils.YamlUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class ZookeeperParams extends StackParams {
    @Resource
    private StackConfiguration stackConfiguration;
    @Resource
    private YamlUtils yamlUtils;
    @Resource
    private StackParams stackParams;

    public String getConfDir() {
        return "/etc/zookeeper/conf";
    }

    public String getZookeeperHome() {
        return stackParams.getStackRoot() + "/" + stackParams.getStackVersion() + "/usr/lib/zookeeper";
    }

    public String getZookeeperCacheHome() {
        return stackConfiguration.getCacheDir() + "/stacks/" + getStackName() + "/" + getStackVersion() + "/services/ZOOKEEPER";
    }

    public String getScriptDir() {
        return getZookeeperCacheHome() + "/scripts";
    }

    public List<Map<String, Object>> getZooCfg() {
        return (List<Map<String, Object>>) yamlUtils.readYaml(getZookeeperCacheHome() + "/configuration/zoo.cfg.yaml", List.class);
    }

    public Map<String, Object> getZookeeperEnv() {
        return (Map<String, Object>) yamlUtils.readYaml(getZookeeperCacheHome() + "/configuration/zookeeper-env.yaml", Map.class);
    }
}
