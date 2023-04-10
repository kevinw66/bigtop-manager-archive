package org.apache.bigtop.manager.mpack.zookeeper;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.mpack.PackageManager;
import org.apache.bigtop.manager.common.shell.ShellExecutor;
import org.apache.bigtop.manager.spi.mpack.Script;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AutoService(Script.class)
public class ZookeeperServerScript implements Script {
    @Resource
    private Params params;

    @Override
    public void install() {
        log.info("install");
        PackageManager packageManager = new PackageManager("zookeeper");
        packageManager.runCommand();
    }

    @Override
    public void configuration() {
        log.info("configuration");
        Map<String, Object> configMap = new HashMap<>();
        for (Map<String, Object> map : params.zooCfg) {
            String key = (String) map.get("name");
            Object value = map.get("value");
            configMap.put(key, value);
        }
        //generate properties file by the map
    }

    @Override
    public void start() {
        log.info("start");
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
