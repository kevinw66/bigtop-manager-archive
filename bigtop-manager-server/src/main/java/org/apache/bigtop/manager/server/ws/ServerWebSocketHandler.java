package org.apache.bigtop.manager.server.ws;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.type.BaseMessage;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import javax.annotation.Resource;

/**
 * WebSocket Endpoint for agent.
 */
@Slf4j
@Component
public class ServerWebSocketHandler extends BinaryWebSocketHandler {

    @Resource
    private MessageDeserializer deserializer;

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
