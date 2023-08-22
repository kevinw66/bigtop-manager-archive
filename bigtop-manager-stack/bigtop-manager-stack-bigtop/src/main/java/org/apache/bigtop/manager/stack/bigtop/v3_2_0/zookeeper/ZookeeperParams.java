package org.apache.bigtop.manager.stack.bigtop.v3_2_0.zookeeper;

import org.apache.bigtop.manager.stack.common.AbstractParams;
import org.apache.bigtop.manager.common.utils.stack.StackConfigUtils;

import java.util.Map;

public class ZookeeperParams extends AbstractParams {

    public static Map<String, Object> zooCfg() {
        return configDict(serviceName(), "zoo.cfg");
    }

    public static Map<String, Object> zookeeperEnv() {
        return configDict(serviceName(), "zookeeper-env");
    }
}
