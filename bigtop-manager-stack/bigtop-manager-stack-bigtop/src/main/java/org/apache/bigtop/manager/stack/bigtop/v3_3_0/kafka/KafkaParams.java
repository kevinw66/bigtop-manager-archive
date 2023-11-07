package org.apache.bigtop.manager.stack.bigtop.v3_3_0.kafka;

import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.stack.common.BaseParams;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;

import java.util.Map;

public class KafkaParams extends BaseParams {

    public static Map<String, Object> kafkaBroker(CommandPayload commandMessage) {
        return LocalSettings.configurations(serviceName(commandMessage), "kafka-broker");
    }

    public static Map<String, Object> kafkaEnv(CommandPayload commandMessage) {
        return LocalSettings.configurations(serviceName(commandMessage), "kafka-env");
    }

    public static Map<String, Object> kafkaLog4j(CommandPayload commandMessage) {
        return LocalSettings.configurations(serviceName(commandMessage), "kafka-log4j");
    }

    public static Map<String, Object> kafkaLimits(CommandPayload commandMessage) {
        return LocalSettings.configurations(serviceName(commandMessage), "kafka.conf");
    }

}
