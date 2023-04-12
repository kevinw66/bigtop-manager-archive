package org.apache.bigtop.manager.server.ws;

import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.type.BaseMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import javax.annotation.Resource;

/**
 * WebSocket Endpoint for agent.
 */
@Component
public class ServerWebSocketHandler extends BinaryWebSocketHandler {

    @Resource
    private MessageDeserializer deserializer;

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        BaseMessage baseMessage = deserializer.deserialize(message.getPayload().array());
        System.out.println(baseMessage.toString());
    }
}
