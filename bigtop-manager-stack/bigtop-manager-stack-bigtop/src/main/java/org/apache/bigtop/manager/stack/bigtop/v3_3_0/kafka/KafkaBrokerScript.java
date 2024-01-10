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

import static org.apache.bigtop.manager.common.constants.Constants.PERMISSION_644;
import static org.apache.bigtop.manager.common.constants.Constants.PERMISSION_755;

@Slf4j
@AutoService(Script.class)
public class KafkaBrokerScript implements Script {

    @Override
    public ShellResult install(BaseParams baseParams) {
        return PackageUtils.install(baseParams.getPackageList());
    }

    @Override
    public ShellResult configure(BaseParams baseParams) {
        KafkaParams kafkaParams = (KafkaParams) baseParams;

        String confDir = kafkaParams.confDir();
        String kafkaUser = kafkaParams.user();
        String kafkaGroup = kafkaParams.group();
        Map<String, Object> kafkaEnv = kafkaParams.kafkaEnv();
        Map<String, Object> kafkaBroker = kafkaParams.kafkaBroker();
        Map<String, Object> kafkaLog4j = kafkaParams.kafkaLog4j();

        LinuxFileUtils.createDirectories(kafkaParams.getKafkaDataDir(), kafkaUser, kafkaGroup, PERMISSION_755, true);
        LinuxFileUtils.createDirectories(kafkaParams.getKafkaLogDir(), kafkaUser, kafkaGroup, PERMISSION_755, true);
        LinuxFileUtils.createDirectories(kafkaParams.getKafkaPidDir(), kafkaUser, kafkaGroup, PERMISSION_755, true);

        // server.properties
        List<String> zookeeperServerHosts = LocalSettings.hosts("zookeeper_server");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("zkHostList", zookeeperServerHosts);
        paramMap.put("host", NetUtils.getHostname());
        LinuxFileUtils.toFile(ConfigType.PROPERTIES,
                MessageFormat.format("{0}/server.properties", confDir),
                kafkaUser,
                kafkaGroup,
                PERMISSION_644,
                kafkaBroker,
                paramMap);

        // env
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("JAVA_HOME", "/usr/local/java");
        modelMap.put("LOG_DIR", kafkaParams.getKafkaLogDir());
        modelMap.put("PID_DIR", kafkaParams.getKafkaPidDir());
        modelMap.put("CONF_DIR", confDir);
        modelMap.put("securityEnabled", false);

        LinuxFileUtils.toFileByTemplate(kafkaParams.getKafkaEnvContent(),
                MessageFormat.format("{0}/kafka-env.sh", confDir),
                kafkaUser,
                kafkaGroup,
                PERMISSION_644,
                modelMap);

        // log4j
        Map<String, Object> log4jMap = Maps.newHashMap(kafkaLog4j);
        log4jMap.remove("content");
        LinuxFileUtils.toFileByTemplate(kafkaParams.getKafkaLog4jContent(),
                MessageFormat.format("{0}/log4j.properties", confDir),
                kafkaUser,
                kafkaGroup,
                PERMISSION_644,
                log4jMap);

        // kafka.limits
        kafkaEnv.put("kafkaUser", kafkaUser);
        kafkaEnv.put("kafkaGroup", kafkaGroup);
        LinuxFileUtils.toFileByTemplate(kafkaParams.getKafkaLimitsContent(),
                MessageFormat.format("{0}/kafka.conf", KafkaParams.LIMITS_CONF_DIR),
                "root",
                "root",
                PERMISSION_644,
                kafkaEnv);

        return DefaultShellResult.success("Kafka Server Configuration success!");
    }

    @Override
    public ShellResult start(BaseParams baseParams) {
        configure(baseParams);
        KafkaParams kafkaParams = (KafkaParams) baseParams;

        String cmd = MessageFormat.format("sh {0}/bin/kafka-server-start.sh {0}/config/server.properties > /dev/null 2>&1 & echo -n $!>{1}",
                kafkaParams.serviceHome(), kafkaParams.getKafkaPidFile());
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
        return LinuxOSUtils.checkProcess(kafkaParams.getKafkaPidFile());
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
