package org.apache.bigtop.manager.server.ws;

import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerWebSocketSessionManager {

    public static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    public static final Map<String, HeartbeatMessage> HEARTBEAT_MESSAGE_MAP = new ConcurrentHashMap<>();
}
