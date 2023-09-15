package org.apache.bigtop.manager.stack.bigtop.v3_2_0.zookeeper;

import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.stack.common.AbstractParams;
import org.apache.bigtop.manager.stack.common.utils.HostCacheUtils;

import java.util.Map;

public class ZookeeperParams extends AbstractParams {

    public static Map<String, Object> zooCfg(CommandMessage commandMessage) {
        return HostCacheUtils.configurations(serviceName(commandMessage), "zoo.cfg");
    }

    public static Map<String, Object> zookeeperEnv(CommandMessage commandMessage) {
        return HostCacheUtils.configurations(serviceName(commandMessage), "zookeeper-env");
    }
}
