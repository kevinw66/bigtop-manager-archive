package org.apache.bigtop.manager.server.configuration;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.ws.ServerWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Resource
    private ServerWebSocketHandler serverWebSocketHandler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(serverWebSocketHandler, "/ws/server");
    }
}
