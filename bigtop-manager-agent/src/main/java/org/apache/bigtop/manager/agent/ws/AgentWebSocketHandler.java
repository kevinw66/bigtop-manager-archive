package org.apache.bigtop.manager.agent.ws;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.configuration.ServerConfiguration;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AgentWebSocketHandler extends AbstractWebSocketHandler implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private ServerConfiguration serverConfiguration;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        executor.scheduleAtFixedRate(() -> {
            try {
                System.out.println("send message: aaa");
                session.sendMessage(new TextMessage("aaa"));
            } catch (IOException e) {
                log.error(MessageFormat.format("Error sending heartbeat to server: {0}", e.getMessage()));
            }
        }, 3, 5, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket connection closed unexpectedly, reconnecting...");
        connectToServer();
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("Bootstrap successfully, connecting to server websocket endpoint...");
        connectToServer();
    }

    private void connectToServer() {
        executor.execute(() -> {
            String uri = "ws://" + serverConfiguration.getHost() + ":" + serverConfiguration.getPort() + "/ws/management";
            StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
            int retryTime = 0;
            while (true) {
                try {
                    webSocketClient.doHandshake(this, uri).get();
                    break;
                } catch (Exception e) {
                    log.error(MessageFormat.format("Error connecting to server: {0}, retry time: {1}", e.getMessage(), ++retryTime));
                }
            }
        });
    }
}
