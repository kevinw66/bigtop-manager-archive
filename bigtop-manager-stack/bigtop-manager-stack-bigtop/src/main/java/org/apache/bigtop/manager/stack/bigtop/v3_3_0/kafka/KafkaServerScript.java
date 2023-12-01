package org.apache.bigtop.manager.stack.bigtop.v3_3_0.kafka;


import com.google.auto.service.AutoService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.NetUtils;
import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.enums.ConfigType;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxFileUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxOSUtils;
import org.apache.bigtop.manager.stack.spi.BaseParams;
import org.apache.bigtop.manager.stack.spi.Script;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.bigtop.manager.stack.bigtop.v3_3_0.kafka.KafkaParams.*;

@Slf4j
@AutoService(Script.class)
public class KafkaServerScript implements Script {

    @Override
    public ShellResult install(BaseParams baseParams) {
        KafkaParams kafkaParams = (KafkaParams) baseParams;
        List<String> packageList = kafkaParams.getPackageList();
        return PackageUtils.install(packageList);
    }

    @Override
    public ShellResult configuration(BaseParams baseParams) {
        KafkaParams kafkaParams = (KafkaParams) baseParams;

        String confDir = kafkaParams.confDir();
        String kafkaUser = kafkaParams.user();
        String kafkaGroup = kafkaParams.group();
        Map<String, Object> kafkaEnv = kafkaParams.kafkaEnv();
        Map<String, Object> kafkaBroker = kafkaParams.kafkaBroker();
        Map<String, Object> kafkaLog4j = kafkaParams.kafkaLog4j();

        LinuxFileUtils.createDirectories(KAFKA_DATA_DIR, kafkaUser, kafkaGroup, "rwxr-xr--", true);
        LinuxFileUtils.createDirectories(KAFKA_LOG_DIR, kafkaUser, kafkaGroup, "rwxr-xr--", true);
        LinuxFileUtils.createDirectories(KAFKA_PID_DIR, kafkaUser, kafkaGroup, "rwxr-xr--", true);


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
        modelMap.put("LOG_DIR", KAFKA_LOG_DIR);
        modelMap.put("PID_DIR", KAFKA_PID_DIR);
        modelMap.put("CONF_DIR", confDir);
        modelMap.put("securityEnabled", false);

        LinuxFileUtils.toFileByTemplate(KAFKA_ENV_CONTENT,
                MessageFormat.format("{0}/kafka-env.sh", confDir),
                kafkaUser,
                kafkaGroup,
                "rw-r--r--",
                modelMap);

        // log4j
        Map<String, Object> log4jMap = Maps.newHashMap(kafkaLog4j);
        log4jMap.remove("content");
        LinuxFileUtils.toFileByTemplate(KAFKA_LOG4J_CONTENT,
                MessageFormat.format("{0}/log4j.properties", confDir),
                kafkaUser,
                kafkaGroup,
                "rw-r--r--",
                log4jMap);

        // kafka.limits
        kafkaEnv.put("kafkaUser", kafkaUser);
        kafkaEnv.put("kafkaGroup", kafkaGroup);
        LinuxFileUtils.toFileByTemplate(KAFKA_LIMITS_CONTENT,
                MessageFormat.format("{0}/kafka.conf", KafkaParams.limitsConfDir),
                "root",
                "root",
                "rw-r--r--",
                kafkaEnv);

        return DefaultShellResult.success("Kafka Server Configuration success!");
    }

    @Override
    public ShellResult start(BaseParams baseParams) {
        configuration(baseParams);
        KafkaParams kafkaParams = (KafkaParams) baseParams;

        String cmd = MessageFormat.format("sh {0}/bin/kafka-server-start.sh {0}/config/server.properties > /dev/null 2>&1 & echo $!>{1}",
                kafkaParams.serviceHome(), KAFKA_PID_FILE);
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, kafkaParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult stop(BaseParams baseParams) {
        KafkaParams kafkaParams = (KafkaParams) baseParams;
        String cmd = MessageFormat.format("sh {0}/bin/kafka-server-stop.sh", kafkaParams.serviceHome());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, kafkaParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult status(BaseParams baseParams) {
        KafkaParams kafkaParams = (KafkaParams) baseParams;

        String cmd = MessageFormat.format("cd {0}; netstat -nalpt | grep 9092", kafkaParams.serviceHome());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, kafkaParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    public ShellResult test(BaseParams baseParams) {
        KafkaParams kafkaParams = (KafkaParams) baseParams;
        try {
            return LinuxOSUtils.sudoExecCmd("date", kafkaParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

}
