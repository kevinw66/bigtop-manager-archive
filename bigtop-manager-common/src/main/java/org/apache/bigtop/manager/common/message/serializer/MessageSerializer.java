package org.apache.bigtop.manager.common.message.serializer;

import org.apache.bigtop.manager.common.message.type.BaseMessage;

public interface MessageSerializer {

    byte[] serialize(BaseMessage message);
}
