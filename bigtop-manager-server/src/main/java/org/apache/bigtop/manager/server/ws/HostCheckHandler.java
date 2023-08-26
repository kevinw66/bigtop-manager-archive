package org.apache.bigtop.manager.server.ws;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.HostCheckMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HostCheckHandler implements Callback {

    @Resource
    private ServerWebSocketHandler serverWebSocketHandler;

    public void checkHost(String hostname) {
        HostCheckMessage hostCheckMessage = new HostCheckMessage();
        hostCheckMessage.setHostname(hostname);
        hostCheckMessage.setHostCheckTypes(HostCheckType.values());
        log.info("Sending host check message: {}", hostCheckMessage);
        serverWebSocketHandler.sendMessage(hostname, hostCheckMessage, this);
    }

    @Override
    public void call(ResultMessage resultMessage) {
        //TODO: 根据枚举大小为前端发送check进度
        if (resultMessage.getCode() == 0) {
            log.info("host check success");
        } else {
            log.error("host check failed");
        }
    }
}
