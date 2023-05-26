package org.apache.bigtop.manager.stack.bigtop.v3_2_0.zookeeper;

import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.apache.bigtop.manager.stack.common.utils.Params;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class ZookeeperParams {

    public static String zookeeperHome() {
        String stack = Params.commandMessage.getStack();
        String version = Params.commandMessage.getVersion();
        String service = Params.commandMessage.getService();

        return "/usr/" + stack.toLowerCase() + "/" + version + "/usr/lib/" + service.toLowerCase();
    }

    public static String confDir() {
        return "/etc/zookeeper/conf";
    }

    public static String stackCacheDir() {
        String stack = Params.commandMessage.getStack();
        String version = Params.commandMessage.getVersion();
        String service = Params.commandMessage.getService();
        String cacheDir = Params.commandMessage.getCacheDir();
        return StringUtils.join(cacheDir + "/stacks/", stack.toUpperCase(), "/", version, "/services/", service.toUpperCase());
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> zooCfg() {
        return YamlUtils.readYaml(stackCacheDir() + "/configuration/zoo.cfg.yaml", List.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> zookeeperEnv() {
        return YamlUtils.readYaml(stackCacheDir() + "/configuration/zookeeper-env.yaml", Map.class);
    }
}
