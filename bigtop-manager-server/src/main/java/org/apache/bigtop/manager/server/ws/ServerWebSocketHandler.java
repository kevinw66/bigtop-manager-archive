package org.apache.bigtop.manager.server.ws;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.bigtop.manager.common.message.type.BaseMessage;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * WebSocket Endpoint for agent.
 */
@Slf4j
@Component
public class ServerWebSocketHandler extends BinaryWebSocketHandler {

    @Resource
    private MessageDeserializer deserializer;

    @Resource
    private MessageSerializer serializer;

    public void sendMessage(String hostname, BaseMessage message) {
        WebSocketSession session = ServerWebSocketSessionManager.SESSIONS.get(hostname);
        try {
            session.sendMessage(new BinaryMessage(serializer.serialize(message)));
        } catch (IOException e) {
            log.error(MessageFormat.format("Error sending message to agent: {0}", e.getMessage()), e);
        }
    }

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
        } else if (baseMessage instanceof ResultMessage) {
            log.info("Received message type: {}", baseMessage.getClass().getSimpleName());
            handleResultMessage((ResultMessage) baseMessage);
        } else {
            log.error("Unrecognized message type: {}", baseMessage.getClass().getSimpleName());
        }
    }

    private void handleResultMessage(ResultMessage resultMessage) {
        //TODO update result message to database
        log.info("handleResultMessage: {}", resultMessage);
    }

    private void handleHeartbeatMessage(WebSocketSession session, HeartbeatMessage heartbeatMessage) {
        HostInfo hostInfo = heartbeatMessage.getHostInfo();
        ServerWebSocketSessionManager.SESSIONS.putIfAbsent(hostInfo.getHostname(), session);
    }
}
