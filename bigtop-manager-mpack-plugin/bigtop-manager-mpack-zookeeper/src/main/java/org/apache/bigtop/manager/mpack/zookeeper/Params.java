package org.apache.bigtop.manager.mpack.zookeeper;

import org.apache.bigtop.manager.common.mpack.MpackConstants;
import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.apache.bigtop.manager.mpack.zookeeper.entity.ZookeeperEnv;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Configuration
public class Params {

    @Resource
    private YamlUtils yamlUtils;

    public static final String ZOOKEEPER_CONF_DIR = "/etc/zookeeper/conf";
    public static final String ZOOKEEPER_HOME = "/usr/bigtop/3.2.0/usr/lib/zookeeper";
    public static final String CMD = String.format("env ZOOCFGDIR=%s ZOOCFG=zoo.cfg %s/zkServer.sh", ZOOKEEPER_CONF_DIR, ZOOKEEPER_HOME);
    public static final String DAEMON_CMD = String.format("source %s/zookeeper-env.sh; %s", ZOOKEEPER_CONF_DIR, CMD);

    ZookeeperEnv zookeeperEnv = (ZookeeperEnv) yamlUtils.loadYaml(MpackConstants.SCRIPT_PATH + "/" + MpackConstants.STACK + "BIGTOP/3.2.0/services/ZOOKEEPER/configuration/zookeeper-env.yaml", ZookeeperEnv.class);
    List<Map<String, Object>> zooCfg = (List<Map<String, Object>>) yamlUtils.loadYaml(MpackConstants.SCRIPT_PATH + "/" + MpackConstants.STACK + "BIGTOP/3.2.0/services/ZOOKEEPER/configuration/zoo.cfg.yaml", List.class);

}
