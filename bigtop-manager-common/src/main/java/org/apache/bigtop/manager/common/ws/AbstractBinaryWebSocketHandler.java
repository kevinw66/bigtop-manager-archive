package org.apache.bigtop.manager.common.ws;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.entity.BaseRequestMessage;
import org.apache.bigtop.manager.common.message.entity.BaseResponseMessage;
import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.bigtop.manager.common.message.entity.BaseMessage;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AbstractBinaryWebSocketHandler extends BinaryWebSocketHandler {

    @Resource
    protected MessageSerializer serializer;

    @Resource
    protected MessageDeserializer deserializer;

    private final ConcurrentHashMap<String, BaseRequestMessage> requests = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, BaseResponseMessage> responses = new ConcurrentHashMap<>();

    protected void sendMessage(WebSocketSession session, BaseMessage message) {
        try {
            session.sendMessage(new BinaryMessage(serializer.serialize(message)));
        } catch (Exception e) {
            log.error("Error sending message: {}", message, e);
        }
    }

    protected BaseResponseMessage sendRequestMessage(WebSocketSession session, BaseRequestMessage request) {
        requests.put(request.getMessageId(), request);

        try {
            session.sendMessage(new BinaryMessage(serializer.serialize(request)));
        } catch (Exception e) {
            log.error("Error sending message: {}", request, e);
        }

        for (int i = 0; i <= 300; i++) {
            BaseResponseMessage response = responses.get(request.getMessageId());
            if (response == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("Error waiting for message response, messageId: {}", request.getMessageId(), e);
                }
            } else {
                requests.remove(request.getMessageId());
                responses.remove(request.getMessageId());
                return response;
            }
        }

        requests.remove(request.getMessageId());
        return null;
    }

    protected void handleResponseMessage(BaseResponseMessage response) {
        if (requests.containsKey(response.getMessageId())) {
            responses.put(response.getMessageId(), response);
        } else {
            log.warn("Message is timed out or unexpected, drop it: {}", response);
        }
    }
}
