package org.apache.bigtop.manager.stack.bigtop.v3_3_0.zookeeper;

import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.stack.spi.BaseParams;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;

import java.util.Map;

public class ZookeeperParams extends BaseParams {

    public ZookeeperParams(CommandPayload commandPayload) {
        super(commandPayload);
    }

    public Map<String, Object> zooCfg() {
        return LocalSettings.configurations(serviceName(), "zoo.cfg");
    }

    public Map<String, Object> zookeeperEnv() {
        return LocalSettings.configurations(serviceName(), "zookeeper-env");
    }
}
