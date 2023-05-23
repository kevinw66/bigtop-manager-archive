package org.apache.bigtop.manager.stack.bigtop320.zookeeper;

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

        return "/usr/" + stack.toUpperCase() + "/" + version + "/usr/lib/" + service.toLowerCase();
    }

    public static String confDir() {
        return "/etc/zookeeper/conf";
    }

    public static String getCacheHome() {
        String stack = Params.commandMessage.getStack();
        String version = Params.commandMessage.getVersion();
        String service = Params.commandMessage.getService();
        return StringUtils.join("/opt/package/code/bigtop-manager/bigtop-manager-server/src/main/resources"+"/stacks/", stack.toUpperCase(), "/", version, "/services/", service.toUpperCase());
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> zooCfg() {
        return YamlUtils.readYaml(getCacheHome() + "/configuration/zoo.cfg.yaml", List.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> zookeeperEnv() {
        return YamlUtils.readYaml(getCacheHome() + "/configuration/zoo-env.yaml", Map.class);
    }
}
