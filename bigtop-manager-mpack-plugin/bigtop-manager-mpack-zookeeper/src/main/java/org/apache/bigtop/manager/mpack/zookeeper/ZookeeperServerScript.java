package org.apache.bigtop.manager.mpack.zookeeper;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.mpack.common.PackageManager;
import org.apache.bigtop.manager.common.shell.ShellExecutor;
import org.apache.bigtop.manager.common.utils.GetBeanUtil;
import org.apache.bigtop.manager.common.utils.PropertiesUtils;
import org.apache.bigtop.manager.spi.mpack.Script;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AutoService(Script.class)
public class ZookeeperServerScript implements Script {

    private Params params = GetBeanUtil.getBean(Params.class);

    private PropertiesUtils propertiesUtils = GetBeanUtil.getBean(PropertiesUtils.class);

    @Override
    public void install() {
        log.info("install");
        System.out.println("install");
        PackageManager packageManager = new PackageManager("zookeeper_3_2_0");
        packageManager.runCommand();
    }

    @Override
    public void configuration() {
        log.info("configuration");
        Map<String, Object> configMap = new HashMap<>();
        for (Map<String, Object> map : params.getZooCfg()) {
            String key = (String) map.get("name");
            Object value = map.get("value");
            configMap.put(key, value);
        }
        //generate properties file by the map
        propertiesUtils.createProperties(params.ZOOKEEPER_CONF_DIR + File.separator + "zoo.cfg", configMap);
    }

    @Override
    public void start() {
        log.info("start");
        System.out.println("DAEMON_CMD: " + params.DAEMON_CMD);
        try {
            ShellExecutor.execCommand(params.DAEMON_CMD + " start");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        log.info("stop");
        try {
            ShellExecutor.execCommand(params.DAEMON_CMD + " stop");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void status() {
        log.info("status");
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
}
