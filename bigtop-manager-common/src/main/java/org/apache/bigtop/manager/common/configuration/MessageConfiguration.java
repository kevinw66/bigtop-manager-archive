package org.apache.bigtop.manager.common.configuration;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MessageConfiguration {

    @Resource
    private ApplicationConfiguration applicationConfiguration;

    @Bean
    public MessageSerializer messageSerializer() throws Exception {
        String serializerType = applicationConfiguration.getSerializer().getType();
        String packageName = "org.apache.bigtop.manager.common.message.serializer";
        String className = packageName + "." + StringUtils.capitalize(serializerType) + "MessageSerializer";
        return (MessageSerializer) Class.forName(className).newInstance();
    }

    @Bean
    public MessageDeserializer messageDeserializer() throws Exception {
        String deserializerType = applicationConfiguration.getSerializer().getType();
        String packageName = "org.apache.bigtop.manager.common.message.serializer";
        String className = packageName + "." + StringUtils.capitalize(deserializerType) + "MessageDeserializer";
        return (MessageDeserializer) Class.forName(className).newInstance();
    }
}
