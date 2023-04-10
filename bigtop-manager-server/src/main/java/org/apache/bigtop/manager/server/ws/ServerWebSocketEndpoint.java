package org.apache.bigtop.manager.server.ws;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * WebSocket Endpoint for front end pages.
 */
@Component
@ServerEndpoint("/ws/server")
public class ServerWebSocketEndpoint {

    @OnOpen
    public void OnOpen(Session session) {
        System.out.println("open");
    }

    @OnClose
    public void OnClose() {
        System.out.println("close");
    }

    @OnMessage
    public void OnMessage(String message) {
        System.out.println(message);
    }
}