package org.apache.bigtop.manager.server.ws;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.bigtop.manager.common.message.type.BaseMessage;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Endpoint for agent.
 */
@Slf4j
@Component
public class ServerWebSocketHandler extends BinaryWebSocketHandler {

    private final ConcurrentHashMap<String, Callback> callbackMap = new ConcurrentHashMap<>();

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

    public void sendMessage(String hostname, BaseMessage message, Callback callback) {
        WebSocketSession session = ServerWebSocketSessionManager.SESSIONS.get(hostname);
        try {
            session.sendMessage(new BinaryMessage(serializer.serialize(message)));
            callbackMap.put(message.getMessageId(), callback);
        } catch (IOException e) {
            log.error(MessageFormat.format("Error sending message to agent: {0}", e.getMessage()), e);
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        BaseMessage baseMessage = deserializer.deserialize(message.getPayload().array());

        handleMessage(session, baseMessage);

        log.debug(baseMessage.toString());
    }

    private void handleMessage(WebSocketSession session, BaseMessage baseMessage) {
        log.info("Received message type: {}", baseMessage.getClass().getSimpleName());
        if (baseMessage instanceof HeartbeatMessage heartbeatMessage) {
            handleHeartbeatMessage(session, heartbeatMessage);
        } else if (baseMessage instanceof ResultMessage resultMessage) {
            handleResultMessage(resultMessage);
        } else {
            log.error("Unrecognized message type: {}", baseMessage.getClass().getSimpleName());
        }
    }

    private void handleResultMessage(ResultMessage resultMessage) {
        log.info("handleResultMessage: {}", resultMessage);
        String messageId = resultMessage.getMessageId();
        callbackMap.get(messageId).call(resultMessage);
        callbackMap.remove(messageId);
    }

    private void handleHeartbeatMessage(WebSocketSession session, HeartbeatMessage heartbeatMessage) {
        HostInfo hostInfo = heartbeatMessage.getHostInfo();
        ServerWebSocketSessionManager.SESSIONS.putIfAbsent(hostInfo.getHostname(), session);
        ServerWebSocketSessionManager.HEARTBEAT_MESSAGE_MAP.putIfAbsent(hostInfo.getHostname(), heartbeatMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.error("session closed: {}, remove it!!!", session.getId());
        ServerWebSocketSessionManager.SESSIONS.values().removeIf(value -> value.getId().equals(session.getId()));
        ServerWebSocketSessionManager.HEARTBEAT_MESSAGE_MAP.clear();
        log.info("latest ServerWebSocketSessionManager.SESSIONS: {}", ServerWebSocketSessionManager.SESSIONS);
    }
}
