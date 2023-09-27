package org.apache.bigtop.manager.agent.ws;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.bigtop.manager.common.message.type.BaseMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class AgentWsTools {

    @Resource
    private MessageSerializer serializer;

    public void sendMessage(WebSocketSession session, BaseMessage message) {
        log.info("send resultMessage to server: {}", message);
        try {
            session.sendMessage(new BinaryMessage(serializer.serialize(message)));
        } catch (Exception e) {
            log.error("Error sending resultMessage to server: {}", message, e);
        }
    }
}
