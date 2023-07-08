package org.apache.bigtop.manager.stack.bigtop.v3_2_0.zookeeper;

import org.apache.bigtop.manager.stack.common.AbstractParams;
import org.apache.bigtop.manager.common.utils.stack.StackConfigUtils;

import java.util.Map;

public class ZookeeperParams extends AbstractParams {

    public static Map<String, Object> zooCfg() {
        return StackConfigUtils.loadConfig(serviceCacheDir() + "/configuration/zoo.cfg.yaml");
    }

    public static Map<String, Object> zookeeperEnv() {
        return StackConfigUtils.loadConfig(serviceCacheDir() + "/configuration/zookeeper-env.yaml");
    }
}
