package org.apache.bigtop.manager.common.config;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MessageConfig {

    @Resource
    private ApplicationConfig applicationConfig;

    @Bean
    public MessageSerializer messageSerializer() throws Exception {
        String serializerType = applicationConfig.getSerializer().getType();
        String packageName = "org.apache.bigtop.manager.common.message.serializer";
        String className = packageName + "." + StringUtils.capitalize(serializerType) + "MessageSerializer";
        return (MessageSerializer) Class.forName(className).getDeclaredConstructor().newInstance();
    }

    @Bean
    public MessageDeserializer messageDeserializer() throws Exception {
        String deserializerType = applicationConfig.getSerializer().getType();
        String packageName = "org.apache.bigtop.manager.common.message.serializer";
        String className = packageName + "." + StringUtils.capitalize(deserializerType) + "MessageDeserializer";
        return (MessageDeserializer) Class.forName(className).getDeclaredConstructor().newInstance();
    }
}
