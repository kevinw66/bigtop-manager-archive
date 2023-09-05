package org.apache.bigtop.manager.stack.bigtop.v3_2_0.zookeeper;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.enums.ConfigType;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxFileUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxOSUtils;
import org.apache.bigtop.manager.stack.spi.Script;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AutoService(Script.class)
public class ZookeeperServerScript implements Script {

    @Override
    public ShellResult install() {
        log.info("ZookeeperServerScript install");
        List<String> packageList = ZookeeperParams.getPackageList();

        return PackageUtils.install(packageList);
    }

    @Override
    public ShellResult configuration() {
        log.info("ZookeeperServerScript configuration");

        String zookeeperUser = ZookeeperParams.user();
        String zookeeperGroup = ZookeeperParams.group();
        String logDir = (String) ZookeeperParams.zookeeperEnv().get("logDir");
        String pidDir = (String) ZookeeperParams.zookeeperEnv().get("pidDir");
        String dataDir = (String) ZookeeperParams.zooCfg().get("dataDir");

        log.info("{}", ZookeeperParams.zooCfg());
        LinuxFileUtils.toFile(ConfigType.PROPERTIES, ZookeeperParams.confDir() + "/zoo.cfg", zookeeperUser, zookeeperGroup, "rw-r--r--", ZookeeperParams.zooCfg());

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("JAVA_HOME", "/usr/local/java");
        modelMap.put("ZOOKEEPER_HOME", ZookeeperParams.serviceHome());
        modelMap.put("ZOO_LOG_DIR", logDir);
        modelMap.put("ZOOPIDFILE", pidDir + "/zookeeper_server.pid");
        modelMap.put("securityEnabled", false);

        log.info("modelMap: {}", modelMap);
        log.info("content: {}", ZookeeperParams.zookeeperEnv().get("content"));
        LinuxFileUtils.toFile(ConfigType.TEMPLATE, ZookeeperParams.confDir() + "/zookeeper-env.sh", zookeeperUser, zookeeperGroup, "rw-r--r--",
                modelMap, ZookeeperParams.zookeeperEnv().get("content").toString());


        LinuxFileUtils.createDirectories(dataDir, zookeeperUser, zookeeperGroup, "rwxr-xr--", true);
        LinuxFileUtils.createDirectories(logDir, zookeeperUser, zookeeperGroup, "rwxr-xr--", true);
        LinuxFileUtils.createDirectories(pidDir, zookeeperUser, zookeeperGroup, "rwxr-xr--", true);

        return new ShellResult(0, "", "");
    }

    @Override
    public ShellResult start() {
        configuration();
        log.info("ZookeeperServerScript start");

        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh start", ZookeeperParams.serviceHome());
        try {
            ShellResult shellResult = LinuxOSUtils.sudoExecCmd(cmd, "zookeeper");
            log.info("[ZookeeperServerScript] [status] output: {}", shellResult);

            return shellResult;
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult stop() {
        log.info("ZookeeperServerScript stop");
        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh stop", ZookeeperParams.serviceHome());
        try {
            ShellResult shellResult = LinuxOSUtils.sudoExecCmd(cmd, "zookeeper");
            log.info("[ZookeeperServerScript] [status] output: {}", shellResult);

            return shellResult;
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult status() {
        log.info("ZookeeperServerScript status");

        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh status", ZookeeperParams.serviceHome());
        try {
            ShellResult shellResult = LinuxOSUtils.sudoExecCmd(cmd, "zookeeper");
            log.info("[ZookeeperServerScript] [status] output: {}", shellResult);

            return shellResult;
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

}
