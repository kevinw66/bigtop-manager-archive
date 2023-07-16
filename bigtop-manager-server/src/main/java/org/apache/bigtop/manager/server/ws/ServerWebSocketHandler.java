package org.apache.bigtop.manager.server.ws;

import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.type.BaseMessage;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;


/**
 * WebSocket Endpoint for agent.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RegisterReflectionForBinding(classes = {
        DefaultSerializers.BigDecimalSerializer.class,
        DefaultSerializers.DateSerializer.class,
        DefaultSerializers.ClassSerializer.class,
        BaseMessage.class,
        HeartbeatMessage.class,
        HostInfo.class
})
public class ServerWebSocketHandler extends BinaryWebSocketHandler {

    private final MessageDeserializer deserializer;

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        BaseMessage baseMessage = deserializer.deserialize(message.getPayload().array());

        handleMessage(session, baseMessage);

        System.out.println(baseMessage.toString());
    }

    private void handleMessage(WebSocketSession session, BaseMessage baseMessage) {
        if (baseMessage instanceof HeartbeatMessage) {
            log.info("Received message type: {}", baseMessage.getClass().getSimpleName());
            handleHeartbeatMessage(session, (HeartbeatMessage) baseMessage);
        } else {
            log.error("Unrecognized message type: {}", baseMessage.getClass().getSimpleName());
        }
    }

    private void handleHeartbeatMessage(WebSocketSession session, HeartbeatMessage heartbeatMessage) {
        HostInfo hostInfo = heartbeatMessage.getHostInfo();
        ServerWebSocketSessionManager.SESSIONS.putIfAbsent(hostInfo.getHostname(), session);
    }
}
