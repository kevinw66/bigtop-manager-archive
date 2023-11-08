package org.apache.bigtop.manager.stack.bigtop.v3_3_0.zookeeper;

import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.stack.common.BaseParams;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;

import java.util.Map;

public class ZookeeperParams extends BaseParams {

    public static Map<String, Object> zooCfg(CommandPayload commandMessage) {
        return LocalSettings.configurations(serviceName(commandMessage), "zoo.cfg");
    }

    public static Map<String, Object> zookeeperEnv(CommandPayload commandMessage) {
        return LocalSettings.configurations(serviceName(commandMessage), "zookeeper-env");
    }
}
