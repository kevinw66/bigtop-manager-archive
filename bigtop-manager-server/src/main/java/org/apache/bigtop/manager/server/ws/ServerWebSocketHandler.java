package org.apache.bigtop.manager.server.ws;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.entity.command.CommandResponseMessage;
import org.apache.bigtop.manager.common.message.entity.BaseMessage;
import org.apache.bigtop.manager.common.message.entity.ComponentHeartbeatMessage;
import org.apache.bigtop.manager.common.message.entity.HeartbeatMessage;
import org.apache.bigtop.manager.common.message.entity.pojo.HostInfo;
import org.apache.bigtop.manager.common.ws.AbstractBinaryWebSocketHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Endpoint for agent.
 */
@Slf4j
@Component
public class ServerWebSocketHandler extends AbstractBinaryWebSocketHandler {

    public static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    public static final Map<String, HeartbeatMessage> HEARTBEAT_MESSAGE_MAP = new ConcurrentHashMap<>();

    public CommandResponseMessage sendMessage(String hostname, BaseMessage message) {
        WebSocketSession session = SESSIONS.get(hostname);
        if (session == null) {
            return null;
        } else {
            return (CommandResponseMessage) super.sendMessage(session, message);
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
        } else if (baseMessage instanceof CommandResponseMessage commandResponseMessage) {
            super.handleResponseMessage(commandResponseMessage);
        } else {
            log.error("Unrecognized message type: {}", baseMessage.getClass().getSimpleName());
        }
    }

    private void handleHeartbeatMessage(WebSocketSession session, HeartbeatMessage heartbeatMessage) {
        HostInfo hostInfo = heartbeatMessage.getHostInfo();
        SESSIONS.putIfAbsent(hostInfo.getHostname(), session);
        HEARTBEAT_MESSAGE_MAP.put(hostInfo.getHostname(), heartbeatMessage);
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
