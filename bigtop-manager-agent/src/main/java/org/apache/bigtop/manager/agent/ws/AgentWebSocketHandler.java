package org.apache.bigtop.manager.agent.ws;

import com.sun.management.OperatingSystemMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.configuration.ApplicationConfiguration;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
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
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentWebSocketHandler extends BinaryWebSocketHandler implements ApplicationListener<ApplicationStartedEvent> {

    private final ApplicationConfiguration applicationConfiguration;

    private final MessageSerializer serializer;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private HostInfo hostInfo;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        executor.scheduleAtFixedRate(() -> {
            try {
                HeartbeatMessage heartbeatMessage = new HeartbeatMessage();
                heartbeatMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
                heartbeatMessage.setHostInfo(hostInfo);
                log.info("HeartbeatMessage: {}", heartbeatMessage);
                session.sendMessage(new BinaryMessage(serializer.serialize(heartbeatMessage)));
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
        readHostInfo();

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
        hostInfo.setName(osmxb.getName());
        hostInfo.setVersion(osmxb.getVersion());
        hostInfo.setArch(osmxb.getArch());
        hostInfo.setAvailableProcessors(osmxb.getAvailableProcessors());
        hostInfo.setProcessCpuTime(osmxb.getProcessCpuTime());
        hostInfo.setTotalPhysicalMemorySize(osmxb.getTotalPhysicalMemorySize());
        hostInfo.setFreePhysicalMemorySize(osmxb.getFreePhysicalMemorySize());
        hostInfo.setTotalSwapSpaceSize(osmxb.getTotalSwapSpaceSize());
        hostInfo.setFreeSwapSpaceSize(osmxb.getFreeSwapSpaceSize());
        hostInfo.setCommittedVirtualMemorySize(osmxb.getCommittedVirtualMemorySize());

        hostInfo.setSystemCpuLoad(new BigDecimal(String.valueOf(osmxb.getSystemCpuLoad())));
        hostInfo.setProcessCpuLoad(new BigDecimal(String.valueOf(osmxb.getProcessCpuLoad())));
        hostInfo.setSystemLoadAverage(new BigDecimal(String.valueOf(osmxb.getSystemLoadAverage())));
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
                    webSocketClient.execute(this, uri).get();
                    break;
                } catch (Exception e) {
                    log.error(MessageFormat.format("Error connecting to server: {0}, retry time: {1}", e.getMessage(), ++retryTime));

                    // retry after 5 seconds
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }
}
