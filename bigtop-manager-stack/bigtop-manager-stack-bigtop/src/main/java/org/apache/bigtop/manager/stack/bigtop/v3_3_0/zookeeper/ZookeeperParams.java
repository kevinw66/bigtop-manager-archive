package org.apache.bigtop.manager.stack.bigtop.v3_3_0.zookeeper;

import lombok.Getter;
import org.apache.bigtop.manager.common.message.entity.payload.CommandPayload;
import org.apache.bigtop.manager.stack.common.utils.BaseParams;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;

import java.util.Map;

@Getter
public class ZookeeperParams extends BaseParams {

    private String zookeeperLogDir = "/var/log/zookeeper";
    private String zookeeperPidDir = "/var/run/zookeeper";
    private String zookeeperDataDir = "/hadoop/zookeeper";
    private String zookeeperPidFile = zookeeperPidDir + "/zookeeper_server.pid";

    public ZookeeperParams(CommandPayload commandPayload) {
        super(commandPayload);
        zookeeperEnv();
        zooCfg();
    }

    public Map<String, Object> zooCfg() {
        Map<String, Object> zooCfg = LocalSettings.configurations(serviceName(), "zoo.cfg");
        zookeeperDataDir = (String) zooCfg.get("dataDir");
        return zooCfg;
    }

    public Map<String, Object> zookeeperEnv() {
        Map<String, Object> zookeeperEnv = LocalSettings.configurations(serviceName(), "zookeeper-env");
        zookeeperLogDir = (String) zookeeperEnv.get("logDir");
        zookeeperPidDir = (String) zookeeperEnv.get("pidDir");
        zookeeperPidFile = zookeeperPidDir + "/zookeeper_server.pid";
        return zookeeperEnv;
    }
}
