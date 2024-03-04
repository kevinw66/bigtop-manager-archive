package org.apache.bigtop.manager.stack.bigtop.v3_3_0.zookeeper;

import lombok.Getter;
import org.apache.bigtop.manager.common.message.entity.payload.CommandPayload;
import org.apache.bigtop.manager.stack.common.annotations.GlobalParams;
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
        globalParamsMap.put("java_home", "/usr/local/java");
        globalParamsMap.put("zookeeper_home", serviceHome());
        globalParamsMap.put("security_enabled", false);
        globalParamsMap.put("zookeeper_pid_file", zookeeperPidFile);
    }

    @GlobalParams
    public Map<String, Object> zooCfg() {
        Map<String, Object> zooCfg = LocalSettings.configurations(serviceName(), "zoo.cfg");
        zookeeperDataDir = (String) zooCfg.get("dataDir");
        return zooCfg;
    }

    @GlobalParams
    public Map<String, Object> zookeeperEnv() {
        Map<String, Object> zookeeperEnv = LocalSettings.configurations(serviceName(), "zookeeper-env");
        zookeeperLogDir = (String) zookeeperEnv.get("zookeeper_log_dir");
        zookeeperPidDir = (String) zookeeperEnv.get("zookeeper_pid_dir");
        zookeeperPidFile = zookeeperPidDir + "/zookeeper_server.pid";
        return zookeeperEnv;
    }
}
