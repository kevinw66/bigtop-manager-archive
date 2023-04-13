package org.apache.bigtop.manager.agent.stack.service.zookeeper;

import org.apache.bigtop.manager.agent.stack.StackParams;
import org.apache.bigtop.manager.agent.utils.YamlUtils;
import org.apache.bigtop.manager.common.configuration.ApplicationConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class ZookeeperParams extends StackParams {
    @Resource
    private ApplicationConfiguration applicationConfiguration;

    @Resource
    private StackParams stackParams;

    public String getConfDir() {
        return "/etc/zookeeper/conf";
    }

    public String getZookeeperHome() {
        return stackParams.getStackRoot() + "/" + stackParams.getStackVersion() + "/usr/lib/zookeeper";
    }

    public String getZookeeperCacheHome() {
        return applicationConfiguration.getStack().getCacheDir() + "/stacks/" + getStackName() + "/" + getStackVersion() + "/services/ZOOKEEPER";
    }

    public String getScriptDir() {
        return getZookeeperCacheHome() + "/scripts";
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getZooCfg() {
        return (List<Map<String, Object>>) YamlUtils.readYaml(getZookeeperCacheHome() + "/configuration/zoo.cfg.yaml", List.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getZookeeperEnv() {
        return (Map<String, Object>) YamlUtils.readYaml(getZookeeperCacheHome() + "/configuration/zookeeper-env.yaml", Map.class);
    }
}
