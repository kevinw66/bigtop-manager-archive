package org.apache.bigtop.manager.stack.bigtop.v3_3_0.kafka;

import lombok.Getter;
import org.apache.bigtop.manager.common.message.entity.payload.CommandPayload;
import org.apache.bigtop.manager.stack.common.annotations.GlobalParams;
import org.apache.bigtop.manager.stack.common.utils.BaseParams;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;

import java.util.Map;

@Getter
public class KafkaParams extends BaseParams {

    private String kafkaLogDir = "/var/log/kafka";
    private String kafkaPidDir = "/var/run/kafka";
    private String kafkaPidFile = "/var/run/kafka/kafka_broker.pid";
    private String kafkaDataDir = "/kafka-logs";
    private String kafkaEnvContent;
    private String kafkaLog4jContent;

    public KafkaParams(CommandPayload commandPayload) {
        super(commandPayload);
        globalParamsMap.put("kafka_user", user());
        globalParamsMap.put("kafka_group", group());
        globalParamsMap.put("java_home", "/usr/local/java");
        globalParamsMap.put("kafka_conf_dir", confDir());
        globalParamsMap.put("security_enabled", false);
    }

    @GlobalParams
    public Map<String, Object> kafkaBroker() {
        Map<String, Object> kafkaBroker = LocalSettings.configurations(serviceName(), "kafka-broker");
        kafkaDataDir = (String) kafkaBroker.get("log.dirs");
        return kafkaBroker;
    }

    @GlobalParams
    public Map<String, Object> kafkaEnv() {
        Map<String, Object> kafkaEnv = LocalSettings.configurations(serviceName(), "kafka-env");
        kafkaPidDir = (String) kafkaEnv.get("kafka_pid_dir");
        kafkaPidFile = kafkaPidDir + "/kafka_broker.pid";
        kafkaLogDir = (String) kafkaEnv.get("kafka_log_dir");
        kafkaEnvContent = (String) kafkaEnv.get("content");
        return kafkaEnv;
    }

    @GlobalParams
    public Map<String, Object> kafkaLog4j() {
        Map<String, Object> kafkaLog4j = LocalSettings.configurations(serviceName(), "kafka-log4j");
        kafkaLog4jContent = (String) kafkaLog4j.get("content");
        return kafkaLog4j;
    }

    public String kafkaLimits() {
        Map<String, Object> kafkaLimits = LocalSettings.configurations(serviceName(), "kafka.conf");
        return (String) kafkaLimits.get("content");
    }

}
