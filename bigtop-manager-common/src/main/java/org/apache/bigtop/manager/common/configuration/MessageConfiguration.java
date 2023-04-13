package org.apache.bigtop.manager.common.configuration;

import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class MessageConfiguration {

    @Resource
    private ApplicationContext applicationContext;

    @Value("${bigtop.manager.serializer.type:kryo}")
    private String type;

    @Bean
    public MessageSerializer messageSerializer() throws Exception {
        String packageName = "org.apache.bigtop.manager.common.message.serializer";
        String className = packageName + "." + StringUtils.capitalize(type) + "MessageSerializer";
        return (MessageSerializer) Class.forName(className).newInstance();
    }

    @Bean
    public MessageDeserializer messageDeserializer() throws Exception {
        String packageName = "org.apache.bigtop.manager.common.message.serializer";
        String className = packageName + "." + StringUtils.capitalize(type) + "MessageDeserializer";
        return (MessageDeserializer) Class.forName(className).newInstance();
    }
}
