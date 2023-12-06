package org.apache.bigtop.manager.agent.ws;

import com.sun.management.OperatingSystemMXBean;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.runner.CommandService;
import org.apache.bigtop.manager.agent.runner.HostCacheService;
import org.apache.bigtop.manager.agent.runner.HostCheckService;
import org.apache.bigtop.manager.common.configuration.ApplicationConfiguration;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.bigtop.manager.common.message.type.*;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.apache.bigtop.manager.common.utils.os.OSDetection;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.apache.bigtop.manager.common.constants.Constants.WS_BINARY_MESSAGE_SIZE_LIMIT;

@Slf4j
@Component
public class AgentWebSocketHandler extends BinaryWebSocketHandler implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private ApplicationConfiguration applicationConfiguration;

    @Resource
    private MessageSerializer serializer;

    @Resource
    private MessageDeserializer deserializer;

    @Resource
    private CommandService commandService;

    @Resource
    private HostCacheService hostCacheService;

    @Resource
    private HostCheckService hostCheckService;

    @Resource
    private AgentWsTools agentWsTools;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private HostInfo hostInfo;

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        BaseMessage baseMessage = deserializer.deserialize(message.getPayload().array());

        handleMessage(session, baseMessage);

    }

    private void handleMessage(WebSocketSession session, BaseMessage baseMessage) {
        log.info("Received message type: {}, session: {}", baseMessage.getClass().getSimpleName(), session);

        if (baseMessage instanceof RequestMessage requestMessage) {
            MessageType messageType = requestMessage.getMessageType();
            switch (messageType) {
                case COMMAND -> commandService.addEvent(requestMessage);
                case HOST_CHECK -> hostCheckService.addEvent(requestMessage);
                case HOST_CACHE -> hostCacheService.addEvent(requestMessage);
            }

        } else {
            log.error("Unrecognized message type: {}", baseMessage.getClass().getSimpleName());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        agentWsTools.session = session;

        executor.scheduleAtFixedRate(() -> {
            try {
                HeartbeatMessage heartbeatMessage = new HeartbeatMessage();
                heartbeatMessage.setHostInfo(hostInfo);

                session.sendMessage(new BinaryMessage(serializer.serialize(heartbeatMessage)));
            } catch (IOException e) {
                log.error(MessageFormat.format("Error sending heartbeat to server: {0}", e.getMessage()));
            }
        }, 3, 5, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket connection closed unexpectedly, reconnecting...");
        agentWsTools.session = null;
        connectToServer();
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        readHostInfo();
        executor.scheduleAtFixedRate(this::readHostInfo, 3, 30, TimeUnit.SECONDS);

        log.info("Bootstrap successfully, connecting to server websocket endpoint...");
        connectToServer();
    }

    private void readHostInfo() {
        hostInfo = new HostInfo();

        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostInfo.setHostname(addr.getHostName());
            hostInfo.setIpv4(addr.getHostAddress());
        } catch (UnknownHostException e) {
            log.error(MessageFormat.format("Error getting host info: {0}", e.getMessage()));
            throw new RuntimeException(e);
        }

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        hostInfo.setOs(OSDetection.getOS());
        hostInfo.setVersion(OSDetection.getOSVersion());
        hostInfo.setArch(OSDetection.getArch());
        hostInfo.setAvailableProcessors(osmxb.getAvailableProcessors());
        hostInfo.setProcessCpuTime(osmxb.getProcessCpuTime());
        hostInfo.setTotalMemorySize(osmxb.getTotalMemorySize());
        hostInfo.setFreeMemorySize(osmxb.getFreeMemorySize());
        hostInfo.setTotalSwapSpaceSize(osmxb.getTotalSwapSpaceSize());
        hostInfo.setFreeSwapSpaceSize(osmxb.getFreeSwapSpaceSize());
        hostInfo.setCommittedVirtualMemorySize(osmxb.getCommittedVirtualMemorySize());

        hostInfo.setCpuLoad(new BigDecimal(String.valueOf(osmxb.getCpuLoad())));
        hostInfo.setProcessCpuLoad(new BigDecimal(String.valueOf(osmxb.getProcessCpuLoad())));
        hostInfo.setSystemLoadAverage(new BigDecimal(String.valueOf(osmxb.getSystemLoadAverage())));

        hostInfo.setFreeDisk(OSDetection.freeDisk());
        hostInfo.setTotalDisk(OSDetection.totalDisk());
    }

    @SuppressWarnings("BusyWait")
    private void connectToServer() {
        executor.execute(() -> {
            String host = applicationConfiguration.getServer().getHost();
            Integer port = applicationConfiguration.getServer().getPort();
            String uri = MessageFormat.format("ws://{0}:{1,number,#}/ws/server", host, port);
            StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
            int retryTime = 0;
            while (true) {
                try {
                    WebSocketSession webSocketSession = webSocketClient.execute(this, uri).get();
                    webSocketSession.setBinaryMessageSizeLimit(WS_BINARY_MESSAGE_SIZE_LIMIT);
                    break;
                } catch (Exception e) {
                    log.error(MessageFormat.format("Error connecting to server: {0}, retry time: {1}", e.getMessage(), ++retryTime));

                    // retry after 5 seconds
                    try {
                        Thread.sleep(Constants.REGISTRY_SESSION_TIMEOUT);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }
}
