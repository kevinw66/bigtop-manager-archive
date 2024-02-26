package org.apache.bigtop.manager.common.message.serializer;

import org.apache.bigtop.manager.common.message.entity.BaseMessage;

public interface MessageDeserializer {

    BaseMessage deserialize(byte[] bytes);
}
