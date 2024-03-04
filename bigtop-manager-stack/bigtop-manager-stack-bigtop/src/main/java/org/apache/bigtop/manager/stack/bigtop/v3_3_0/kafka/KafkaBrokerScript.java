package org.apache.bigtop.manager.stack.bigtop.v3_3_0.kafka;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.spi.stack.Params;
import org.apache.bigtop.manager.spi.stack.Script;
import org.apache.bigtop.manager.stack.common.enums.ConfigType;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxFileUtils;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxOSUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.bigtop.manager.common.constants.Constants.*;

@Slf4j
@AutoService(Script.class)
public class KafkaBrokerScript implements Script {

    @Override
    public ShellResult install(Params params) {
        return PackageUtils.install(params.getPackageList());
    }

    @Override
    public ShellResult configure(Params params) {
        KafkaParams kafkaParams = (KafkaParams) params;

        String confDir = kafkaParams.confDir();
        String kafkaUser = kafkaParams.user();
        String kafkaGroup = kafkaParams.group();

        LinuxFileUtils.createDirectories(kafkaParams.getKafkaDataDir(), kafkaUser, kafkaGroup, PERMISSION_755, true);
        LinuxFileUtils.createDirectories(kafkaParams.getKafkaLogDir(), kafkaUser, kafkaGroup, PERMISSION_755, true);
        LinuxFileUtils.createDirectories(kafkaParams.getKafkaPidDir(), kafkaUser, kafkaGroup, PERMISSION_755, true);

        // server.properties
        List<String> zookeeperServerHosts = LocalSettings.hosts("zookeeper_server");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("zk_server_list", zookeeperServerHosts);
        paramMap.put("host", kafkaParams.hostname());
        LinuxFileUtils.toFile(ConfigType.PROPERTIES,
                MessageFormat.format("{0}/server.properties", confDir),
                kafkaUser,
                kafkaGroup,
                PERMISSION_644,
                kafkaParams.kafkaBroker(),
                paramMap);

        // kafka-env
        LinuxFileUtils.toFileByTemplate(kafkaParams.getKafkaEnvContent(),
                MessageFormat.format("{0}/kafka-env.sh", confDir),
                kafkaUser,
                kafkaGroup,
                PERMISSION_644,
                kafkaParams.getGlobalParamsMap());

        // log4j
        LinuxFileUtils.toFileByTemplate(kafkaParams.getKafkaLog4jContent(),
                MessageFormat.format("{0}/log4j.properties", confDir),
                kafkaUser,
                kafkaGroup,
                PERMISSION_644,
                kafkaParams.getGlobalParamsMap());

        // kafka.limits
        LinuxFileUtils.toFileByTemplate(kafkaParams.kafkaLimits(),
                MessageFormat.format("{0}/kafka.conf", KafkaParams.LIMITS_CONF_DIR),
                ROOT_USER,
                ROOT_USER,
                PERMISSION_644,
                kafkaParams.getGlobalParamsMap());

        return DefaultShellResult.success("Kafka Server Configure success!");
    }

    @Override
    public ShellResult start(Params params) {
        configure(params);
        KafkaParams kafkaParams = (KafkaParams) params;

        String cmd = MessageFormat.format("sh {0}/bin/kafka-server-start.sh {0}/config/server.properties > /dev/null 2>&1 & echo -n $!>{1}",
                kafkaParams.serviceHome(), kafkaParams.getKafkaPidFile());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, kafkaParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult stop(Params params) {
        KafkaParams kafkaParams = (KafkaParams) params;
        String cmd = MessageFormat.format("sh {0}/bin/kafka-server-stop.sh", kafkaParams.serviceHome());
        try {
            return LinuxOSUtils.sudoExecCmd(cmd, kafkaParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    @Override
    public ShellResult status(Params params) {
        KafkaParams kafkaParams = (KafkaParams) params;
        return LinuxOSUtils.checkProcess(kafkaParams.getKafkaPidFile());
    }

    public ShellResult test(Params params) {
        KafkaParams kafkaParams = (KafkaParams) params;
        try {
            return LinuxOSUtils.sudoExecCmd("date", kafkaParams.user());
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

}
