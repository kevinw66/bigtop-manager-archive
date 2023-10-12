/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.bigtop.manager.server.subscriber;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.HostCheckMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;
import org.apache.bigtop.manager.server.model.event.HostAddedEvent;
import org.apache.bigtop.manager.server.ws.Callback;
import org.apache.bigtop.manager.server.ws.ServerWebSocketHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HostEventSubscriber implements Callback {

    private final Map<String, CountDownLatch> messageLatchMap = new HashMap<>();

    @Resource
    private AsyncEventBus asyncEventBus;

    @Resource
    private ServerWebSocketHandler serverWebSocketHandler;

    @PostConstruct
    public void init() {
        asyncEventBus.register(this);
    }

    @Subscribe
    public void onHostAdded(HostAddedEvent event) {
        List<String> hostnames = event.getHostnames();

        for (String hostname : hostnames) {
            HostCheckMessage message = new HostCheckMessage();
            message.setHostname(hostname);
            message.setHostCheckTypes(HostCheckType.values());

            log.info("Sending host check message: {}", message);
            serverWebSocketHandler.sendMessage(hostname, message, this);

            CountDownLatch countDownLatch = messageLatchMap.computeIfAbsent(message.getMessageId(), k -> new CountDownLatch(1));
            try {
                boolean timeoutFlag = countDownLatch.await(30, TimeUnit.SECONDS);
                if (!timeoutFlag) {
                    log.error("execute task timeout");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void call(ResultMessage resultMessage) {
        CountDownLatch countDownLatch = messageLatchMap.get(resultMessage.getMessageId());
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }
}
