package org.apache.bigtop.manager.mpack.zookeeper;

import org.apache.bigtop.manager.common.mpack.MpackConstants;
import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.apache.bigtop.manager.mpack.zookeeper.entity.ZookeeperServiceEnv;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class Params {

    @Resource
    private YamlUtils yamlUtils;

    public String ZOOKEEPER_CONF_DIR = "/etc/zookeeper/conf";
    public String ZOOKEEPER_HOME = "/usr/bigtop/3.2.0/usr/lib/zookeeper";
    public String CMD = String.format("env ZOOCFGDIR=%s ZOOCFG=zoo.cfg %s/bin/zkServer.sh", ZOOKEEPER_CONF_DIR, ZOOKEEPER_HOME);
    public String DAEMON_CMD = String.format("source %s/zookeeper-env.sh; %s", ZOOKEEPER_CONF_DIR, CMD);


    public ZookeeperServiceEnv getZookeeperEnv() {
        return (ZookeeperServiceEnv) yamlUtils.loadYaml(MpackConstants.ABSOLUTE_STACK_PATH + "/BIGTOP/3.2.0/services/ZOOKEEPER/configuration/zookeeper-env.yaml", ZookeeperServiceEnv.class);
    }

    public List<Map<String, Object>> getZooCfg() {
        List<Map<String, Object>> zooCfg = (List<Map<String, Object>>) yamlUtils.loadYaml(MpackConstants.ABSOLUTE_STACK_PATH + "/BIGTOP/3.2.0/services/ZOOKEEPER/configuration/zoo.cfg.yaml", List.class);
        return zooCfg;
    }


}
