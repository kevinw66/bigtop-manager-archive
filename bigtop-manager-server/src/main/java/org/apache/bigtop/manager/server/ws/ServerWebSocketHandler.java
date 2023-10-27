package org.apache.bigtop.manager.server.ws;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.bigtop.manager.common.message.type.BaseMessage;
import org.apache.bigtop.manager.common.message.type.ComponentHeartbeatMessage;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Endpoint for agent.
 */
@Slf4j
@Component
public class ServerWebSocketHandler extends BinaryWebSocketHandler {

    public static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    public static final Map<String, HeartbeatMessage> HEARTBEAT_MESSAGE_MAP = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ResultMessage> resMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Callback> callbackMap = new ConcurrentHashMap<>();

    @Resource
    private MessageDeserializer deserializer;

    @Resource
    private MessageSerializer serializer;

    public ResultMessage sendMessage(String hostname, BaseMessage message) {
        WebSocketSession session = SESSIONS.get(hostname);
        if (session == null) {
            return null;
        }

        try {
            session.sendMessage(new BinaryMessage(serializer.serialize(message)));
            for (int i = 0; i <= 300; i++) {
                ResultMessage result = resMap.get(message.getMessageId());
                if (result == null) {
                    Thread.sleep(1000);
                } else {
                    resMap.remove(message.getMessageId());
                    return result;
                }
            }
        } catch (Exception e) {
            log.error(MessageFormat.format("Error sending message to agent: {0}", e.getMessage()), e);
        }

        return null;
    }

    public void sendMessage(String hostname, BaseMessage message, Callback callback) {
        WebSocketSession session = SESSIONS.get(hostname);
        try {
            session.sendMessage(new BinaryMessage(serializer.serialize(message)));
            callbackMap.put(message.getMessageId(), callback);
        } catch (IOException e) {
            log.error(MessageFormat.format("Error sending message to agent: {0}", e.getMessage()), e);
        }
    }

    @Override
    protected void handleBinaryMessage(@Nonnull WebSocketSession session, BinaryMessage message) throws Exception {
        BaseMessage baseMessage = deserializer.deserialize(message.getPayload().array());

        handleMessage(session, baseMessage);

        log.debug(baseMessage.toString());
    }

    private void handleMessage(WebSocketSession session, BaseMessage baseMessage) {
        log.info("Received message type: {}", baseMessage.getClass().getSimpleName());
        if (baseMessage instanceof HeartbeatMessage heartbeatMessage) {
            handleHeartbeatMessage(session, heartbeatMessage);
        } else if (baseMessage instanceof ComponentHeartbeatMessage heartbeatMessage) {
            handleComponentHeartbeatMessage(heartbeatMessage);
        } else if (baseMessage instanceof ResultMessage resultMessage) {
            handleResultMessage(resultMessage);
        } else {
            log.error("Unrecognized message type: {}", baseMessage.getClass().getSimpleName());
        }
    }

    private void handleResultMessage(ResultMessage resultMessage) {
        String messageId = resultMessage.getMessageId();
        Callback callback = callbackMap.get(messageId);
        if (callback == null) {
            resMap.put(messageId, resultMessage);
        } else {
            callback.call(resultMessage);
            callbackMap.remove(messageId);
        }
    }

    private void handleHeartbeatMessage(WebSocketSession session, HeartbeatMessage heartbeatMessage) {
        HostInfo hostInfo = heartbeatMessage.getHostInfo();
        SESSIONS.putIfAbsent(hostInfo.getHostname(), session);
        HEARTBEAT_MESSAGE_MAP.putIfAbsent(hostInfo.getHostname(), heartbeatMessage);
    }

    private void handleComponentHeartbeatMessage(ComponentHeartbeatMessage heartbeatMessage) {
        log.info("received component heartbeat message: {}", heartbeatMessage);
        // if code is 0, it means the component is running, otherwise it is not running.
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @Nonnull CloseStatus status) throws Exception {
        log.error("session closed: {}, remove it!!!", session.getId());
        SESSIONS.values().removeIf(value -> value.getId().equals(session.getId()));
        HEARTBEAT_MESSAGE_MAP.clear();
        log.info("latest ServerWebSocketSessionManager.SESSIONS: {}", SESSIONS);
    }
}
