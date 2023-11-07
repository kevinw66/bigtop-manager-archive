package org.apache.bigtop.manager.stack.bigtop.v3_3_0.zookeeper;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.utils.NetUtils;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.enums.ConfigType;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxFileUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxOSUtils;
import org.apache.bigtop.manager.stack.spi.Script;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
@AutoService(Script.class)
public class ZookeeperServerScript implements Script {

    @Override
    public ShellResult install(CommandPayload commandMessage) {
        log.info("ZookeeperServerScript install");
        List<String> packageList = ZookeeperParams.getPackageList(commandMessage);

        return PackageUtils.install(packageList);
    }

    @Override
    public ShellResult configuration(CommandPayload commandMessage) {
        log.info("ZookeeperServerScript configuration");

        String confDir = ZookeeperParams.confDir(commandMessage);
        String zookeeperUser = ZookeeperParams.user(commandMessage);
        String zookeeperGroup = ZookeeperParams.group(commandMessage);
        Map<String, Object> zookeeperEnv = ZookeeperParams.zookeeperEnv(commandMessage);
        Map<String, Object> zooCfg = ZookeeperParams.zooCfg(commandMessage);
        List<String> zkHostList = LocalSettings.hosts("ZOOKEEPER_SERVER");

        String logDir = (String) zookeeperEnv.get("logDir");
        String pidDir = (String) zookeeperEnv.get("pidDir");
        String dataDir = (String) zooCfg.get("dataDir");
        LinuxFileUtils.createDirectories(dataDir, zookeeperUser, zookeeperGroup, "rwxr-xr--", true);
        LinuxFileUtils.createDirectories(logDir, zookeeperUser, zookeeperGroup, "rwxr-xr--", true);
        LinuxFileUtils.createDirectories(pidDir, zookeeperUser, zookeeperGroup, "rwxr-xr--", true);

        log.info("{}", ZookeeperParams.zooCfg(commandMessage));
        //针对zkHostList排序，获取当前hostname的index+1
        //server.${host?index+1}=${host}:2888:3888
        zkHostList.sort(String::compareToIgnoreCase);
        StringBuilder zkServerStr = new StringBuilder();
        for (String zkHost : zkHostList) {
            zkServerStr.append(MessageFormat.format("server.{0}={1}:2888:3888", zkHostList.indexOf(zkHost) + 1, zkHost))
                    .append("\n");
        }

        LinuxFileUtils.toFile(ConfigType.CONTENT, MessageFormat.format("{0}/myid", dataDir), zookeeperUser, zookeeperGroup,
                "rw-r--r--", zkHostList.indexOf(NetUtils.getHostname()) + 1 + "");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("zkServerStr", zkServerStr.toString());
        paramMap.put("securityEnabled", false);
        String templateContent = zooCfg.get("templateContent").toString();
        zooCfg.remove("templateContent");
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("model", zooCfg);

        LinuxFileUtils.toFileByTemplate(templateContent, MessageFormat.format("{0}/zoo.cfg", confDir),
                zookeeperUser, zookeeperGroup, "rw-r--r--", modelMap, paramMap);

        Map<String, Object> envMap = new HashMap<>();
        envMap.put("JAVA_HOME", "/usr/local/java");
        envMap.put("ZOOKEEPER_HOME", ZookeeperParams.serviceHome(commandMessage));
        envMap.put("ZOO_LOG_DIR", logDir);
        envMap.put("ZOOPIDFILE", pidDir + "/zookeeper_server.pid");
        envMap.put("securityEnabled", false);

        log.info("modelMap: {}", envMap);
        log.info("content: {}", zookeeperEnv.get("content"));
        LinuxFileUtils.toFileByTemplate(zookeeperEnv.get("content").toString(), MessageFormat.format("{0}/zookeeper-env.sh", confDir),
                zookeeperUser, zookeeperGroup, "rw-r--r--", envMap);

        return new ShellResult(0, "", "");
    }

    @Override
    public ShellResult start(CommandPayload commandMessage) {
        configuration(commandMessage);
        log.info("ZookeeperServerScript start");

        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh start", ZookeeperParams.serviceHome(commandMessage));
        try {
            ShellResult shellResult = LinuxOSUtils.sudoExecCmd(cmd, "zookeeper");
            log.info("[ZookeeperServerScript] [status] output: {}", shellResult);

            return shellResult;
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult stop(CommandPayload commandMessage) {
        log.info("ZookeeperServerScript stop");
        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh stop", ZookeeperParams.serviceHome(commandMessage));
        try {
            ShellResult shellResult = LinuxOSUtils.sudoExecCmd(cmd, "zookeeper");
            log.info("[ZookeeperServerScript] [status] output: {}", shellResult);

            return shellResult;
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult status(CommandPayload commandMessage) {
        log.info("ZookeeperServerScript status");

        String cmd = MessageFormat.format("sh {0}/bin/zkServer.sh status", ZookeeperParams.serviceHome(commandMessage));
        try {
            ShellResult shellResult = LinuxOSUtils.sudoExecCmd(cmd, "zookeeper");
            log.info("[ZookeeperServerScript] [status] output: {}", shellResult);

            return shellResult;
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

}
