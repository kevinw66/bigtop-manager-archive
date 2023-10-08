package org.apache.bigtop.manager.server.config;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.ws.DefaultWebSocketHandler;
import org.apache.bigtop.manager.server.ws.ServerWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import static org.apache.bigtop.manager.common.constants.Constants.WS_BINARY_MESSAGE_SIZE_LIMIT;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private DefaultWebSocketHandler defaultWebSocketHandler;

    @Resource
    private ServerWebSocketHandler serverWebSocketHandler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(defaultWebSocketHandler, "/ws/default");
        registry.addHandler(serverWebSocketHandler, "/ws/server");
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(WS_BINARY_MESSAGE_SIZE_LIMIT);
        return container;
    }
}
