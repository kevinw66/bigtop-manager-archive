package org.apache.bigtop.manager.stack.bigtop.v3_3_0.kafka;

import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.stack.spi.BaseParams;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;

import java.util.Map;

public class KafkaParams extends BaseParams {

    public static String KAFKA_LOG_DIR = "/var/log/kafka";
    public static String KAFKA_PID_DIR = "/var/run/kafka";
    public static String KAFKA_PID_FILE = "/var/run/kafka/kafka.pid";
    public static String KAFKA_DATA_DIR = "/kafka-logs";
    public static String KAFKA_ENV_CONTENT;
    public static String KAFKA_LOG4J_CONTENT;
    public static String KAFKA_LIMITS_CONTENT;

    public KafkaParams(CommandPayload commandPayload) {
        super(commandPayload);
        kafkaBroker();
        kafkaEnv();
        kafkaLog4j();
        kafkaLimits();
    }

    public Map<String, Object> kafkaBroker() {
        Map<String, Object> kafkaBroker = LocalSettings.configurations(serviceName(), "kafka-broker");
        KAFKA_DATA_DIR = (String) kafkaBroker.get("log.dirs");
        return kafkaBroker;
    }

    public Map<String, Object> kafkaEnv() {
        Map<String, Object> kafkaEnv = LocalSettings.configurations(serviceName(), "kafka-env");
        KAFKA_PID_DIR = (String) kafkaEnv.get("pidDir");
        KAFKA_PID_FILE = KAFKA_PID_DIR + "/kafka_server.pid";
        KAFKA_LOG_DIR = (String) kafkaEnv.get("logDir");
        KAFKA_ENV_CONTENT = (String) kafkaEnv.get("content");
        return kafkaEnv;
    }

    public Map<String, Object> kafkaLog4j() {
        Map<String, Object> kafkaLog4j = LocalSettings.configurations(serviceName(), "kafka-log4j");
        KAFKA_LOG4J_CONTENT = (String) kafkaLog4j.get("content");
        return kafkaLog4j;
    }

    public void kafkaLimits() {
        Map<String, Object> kafkaLimits = LocalSettings.configurations(serviceName(), "kafka.conf");
        KAFKA_LIMITS_CONTENT = (String) kafkaLimits.get("content");
    }

}
