package org.apache.bigtop.manager.stack.bigtop.v3_3_0.kafka;


import com.google.auto.service.AutoService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.utils.NetUtils;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.bigtop.v3_3_0.zookeeper.ZookeeperParams;
import org.apache.bigtop.manager.stack.common.enums.ConfigType;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;
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
public class KafkaServerScript implements Script {

    @Override
    public ShellResult install(CommandPayload commandMessage) {
        log.info("KafkaServerScript install");
        List<String> packageList = KafkaParams.getPackageList(commandMessage);
        return PackageUtils.install(packageList);
    }

    @Override
    public ShellResult configuration(CommandPayload commandMessage) {
        log.info("KafkaServerScript configuration");

        String confDir = KafkaParams.confDir(commandMessage);
        String kafkaUser = KafkaParams.user(commandMessage);
        String kafkaGroup = KafkaParams.group(commandMessage);
        Map<String, Object> kafkaEnv = KafkaParams.kafkaEnv(commandMessage);
        Map<String, Object> kafkaBroker = KafkaParams.kafkaBroker(commandMessage);
        Map<String, Object> kafkaLog4j = KafkaParams.kafkaLog4j(commandMessage);
        Map<String, Object> kafkaLimits = KafkaParams.kafkaLimits(commandMessage);

        String logDir = (String) kafkaEnv.get("logDir");
        String pidDir = (String) kafkaEnv.get("pidDir");
        String content = (String) kafkaEnv.get("content");
        String dataDir = (String) kafkaBroker.get("log.dirs");
        String log4jContent = (String) kafkaLog4j.get("content");
        String kafkaLimitsContent = (String) kafkaLimits.get("content");

        LinuxFileUtils.createDirectories(dataDir, kafkaUser, kafkaGroup, "rwxr-xr--", true);
        LinuxFileUtils.createDirectories(logDir, kafkaUser, kafkaGroup, "rwxr-xr--", true);
        LinuxFileUtils.createDirectories(pidDir, kafkaUser, kafkaGroup, "rwxr-xr--", true);


        // server.properties
        List<String> zookeeperServerHosts = LocalSettings.hosts("ZOOKEEPER_SERVER");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("zkHostList", zookeeperServerHosts);
        paramMap.put("host", NetUtils.getHostname());
        LinuxFileUtils.toFile(ConfigType.PROPERTIES,
                MessageFormat.format("{0}/server.properties", confDir),
                kafkaUser,
                kafkaGroup,
                "rw-r--r--",
                kafkaBroker,
                paramMap);

        // env
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("JAVA_HOME", "/usr/local/java");
        modelMap.put("LOG_DIR", logDir);
        modelMap.put("PID_DIR", pidDir + "/kafka.pid");
        modelMap.put("CONF_DIR", confDir);
        modelMap.put("securityEnabled", false);

        LinuxFileUtils.toFileByTemplate(content,
                MessageFormat.format("{0}/kafka-env.sh", confDir),
                kafkaUser,
                kafkaGroup,
                "rw-r--r--",
                modelMap);

        // log4j
        Map<String, Object> log4jMap = Maps.newHashMap(kafkaLog4j);
        log4jMap.remove("content");
        LinuxFileUtils.toFileByTemplate(log4jContent,
                MessageFormat.format("{0}/log4j.properties", confDir),
                kafkaUser,
                kafkaGroup,
                "rw-r--r--",
                log4jMap);

        // kafka.limits
        kafkaEnv.put("kafkaUser", kafkaUser);
        kafkaEnv.put("kafkaGroup", kafkaGroup);
        LinuxFileUtils.toFileByTemplate(kafkaLimitsContent,
                MessageFormat.format("{0}/kafka.conf", KafkaParams.limitsConfDir),
                "root",
                "root",
                "rw-r--r--",
                kafkaEnv);

        return new ShellResult(0, "configuration complete!!!", "");
    }

    @Override
    public ShellResult start(CommandPayload commandMessage) {
        configuration(commandMessage);
        log.info("KafkaServerScript start");

        String cmd = MessageFormat.format("sh {0}/bin/kafka-server-start.sh -daemon {0}/config/server.properties",
                KafkaParams.serviceHome(commandMessage));
        try {
            ShellResult shellResult = LinuxOSUtils.sudoExecCmd(cmd, commandMessage.getServiceUser());
            log.info("[KafkaServerScript] [start] output: {}", shellResult);

            return shellResult;
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult stop(CommandPayload commandMessage) {
        log.info("KafkaServerScript stop");
        String cmd = MessageFormat.format("sh {0}/bin/kafka-server-stop.sh", KafkaParams.serviceHome(commandMessage));
        try {
            ShellResult shellResult = LinuxOSUtils.sudoExecCmd(cmd, commandMessage.getServiceUser());
            log.info("[KafkaServerScript] [stop] output: {}", shellResult);

            return shellResult;
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult status(CommandPayload commandMessage) {
        log.info("KafkaServerScript status");

        String cmd = MessageFormat.format("cd {0}; netstat -nalpt | grep 9092", ZookeeperParams.serviceHome(commandMessage));
        try {
            ShellResult shellResult = LinuxOSUtils.sudoExecCmd(cmd, commandMessage.getServiceUser());
            log.info("[KafkaServerScript] [status] output: {}", shellResult);

            return shellResult;
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    public ShellResult test(CommandPayload commandMessage) {
        try {
            ShellResult shellResult = LinuxOSUtils.sudoExecCmd("date", commandMessage.getServiceUser());
            return shellResult;
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

}
