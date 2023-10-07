package org.apache.bigtop.manager.server.ws;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.HostCheckMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;
import org.apache.bigtop.manager.server.model.event.HostCheckEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HostCheckHandler implements Callback {

    @Resource
    private AsyncEventBus asyncEventBus;

    @PostConstruct
    public void init() {
        asyncEventBus.register(this);
    }

    @Resource
    private ServerWebSocketHandler serverWebSocketHandler;

    @Subscribe
    public void checkHost(HostCheckEvent hostCheckEvent) {
        List<String> hostnameList = hostCheckEvent.getHostnames();

        for (String hostname : hostnameList) {
            HostCheckMessage hostCheckMessage = convertMessage(hostname);
            log.info("Sending host check message: {}", hostCheckMessage);
            serverWebSocketHandler.sendMessage(hostname, hostCheckMessage, this);
        }

        countDownLatch = new CountDownLatch(hostnameList.size() * HostCheckType.values().length);
        try {
            boolean timeoutFlag = countDownLatch.await(30, TimeUnit.SECONDS);
            if (!timeoutFlag) {
                log.error("execute task timeout");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HostCheckMessage convertMessage(String hostname) {
        HostCheckMessage hostCheckMessage = new HostCheckMessage();
        hostCheckMessage.setHostname(hostname);
        hostCheckMessage.setHostCheckTypes(HostCheckType.values());
        return hostCheckMessage;
    }

    private CountDownLatch countDownLatch;

    @Override
    public void call(ResultMessage resultMessage) {
        countDownLatch.countDown();
        //TODO: 根据枚举大小为前端发送check进度
        if (resultMessage.getCode() == 0) {
            log.info("Host check success, {}", resultMessage);
        } else {
            log.error("Host check failed, {}", resultMessage);
        }
    }
}
