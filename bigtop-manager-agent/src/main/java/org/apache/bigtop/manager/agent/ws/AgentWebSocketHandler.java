package org.apache.bigtop.manager.agent.ws;

import com.sun.management.OperatingSystemMXBean;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.configuration.ApplicationConfiguration;
import org.apache.bigtop.manager.common.message.serializer.MessageDeserializer;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.bigtop.manager.common.message.type.*;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.os.OSDetection;
import org.apache.bigtop.manager.common.utils.os.TimeSyncDetection;
import org.apache.bigtop.manager.common.utils.shell.ShellExecutor;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxFileUtils;
import org.apache.bigtop.manager.stack.core.Executor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.apache.bigtop.manager.common.constants.HostCacheConstants.*;

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
    private Executor stackExecutor;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private HostInfo hostInfo;

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        BaseMessage baseMessage = deserializer.deserialize(message.getPayload().array());

        handleMessage(session, baseMessage);

        System.out.println(baseMessage.toString());
    }

    private void handleMessage(WebSocketSession session, BaseMessage baseMessage) {
        if (baseMessage instanceof CommandMessage commandMessage) {
            log.info("Received message type: {}", commandMessage.getClass().getSimpleName());
            handleCommandMessage(session, commandMessage);
        } else if (baseMessage instanceof HostCacheMessage hostCacheMessage) {
            log.info("Received message type: {}", hostCacheMessage.getClass().getSimpleName());
            handleHostCacheMessage(session, hostCacheMessage);
        } else if (baseMessage instanceof HostCheckMessage hostCheckMessage) {
            log.info("Received message type: {}", hostCheckMessage.getClass().getSimpleName());
            handleHostCheckMessage(session, hostCheckMessage);
        } else {
            log.error("Unrecognized message type: {}", baseMessage.getClass().getSimpleName());
        }
    }

    private void handleHostCheckMessage(WebSocketSession session, HostCheckMessage hostCheckMessage) {
        HostCheckType[] hostCheckTypes = hostCheckMessage.getHostCheckTypes();

        for (HostCheckType hostCheckType : hostCheckTypes) {
            switch (hostCheckType) {
                case TIME_SYNC -> {
                    ResultMessage resultMessage = new ResultMessage();
                    ShellResult shellResult = TimeSyncDetection.checkTimeSync();

                    resultMessage.setCode(shellResult.getExitCode());
                    resultMessage.setMessageId(hostCheckMessage.getMessageId());
                    resultMessage.setHostname(hostCheckMessage.getHostname());

                    try {
                        session.sendMessage(new BinaryMessage(serializer.serialize(resultMessage)));
                    } catch (IOException e) {
                        log.error(MessageFormat.format("Error sending resultMessage to server: {0}", e.getMessage()));
                    }
                }

            }
        }

    }

    private void handleHostCacheMessage(WebSocketSession session, HostCacheMessage hostCacheMessage) {
        String cacheDir = hostCacheMessage.getCacheDir();

        LinuxFileUtils.createDirectories(cacheDir, "root", "root", "rwxr-xr-x", false);

        JsonUtils.writeJson(cacheDir + BASIC_INFO, hostCacheMessage.getBasicInfo());
        JsonUtils.writeJson(cacheDir + CLUSTER_INFO, hostCacheMessage.getClusterInfo());
        JsonUtils.writeJson(cacheDir + CONFIGURATIONS_INFO, hostCacheMessage.getConfigurations());
        JsonUtils.writeJson(cacheDir + HOSTS_INFO, hostCacheMessage.getClusterHostInfo());
        JsonUtils.writeJson(cacheDir + REPOS_INFO, hostCacheMessage.getRepoInfo());
        JsonUtils.writeJson(cacheDir + USERS_INFO, hostCacheMessage.getUserInfo());

        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setMessageId(hostCacheMessage.getMessageId());
        resultMessage.setHostname(hostCacheMessage.getHostname());
        resultMessage.setCode(0);
        try {
            session.sendMessage(new BinaryMessage(serializer.serialize(resultMessage)));
        } catch (IOException e) {
            log.error(MessageFormat.format("Error sending resultMessage to server: {0}", e.getMessage()));
        }
    }

    private void handleCommandMessage(WebSocketSession session, CommandMessage commandMessage) {
        Object result = stackExecutor.execute(commandMessage);
        if (result instanceof ShellResult shellResult) {
            ResultMessage resultMessage = new ResultMessage();
            resultMessage.setCode(shellResult.getExitCode());
            resultMessage.setResult(shellResult.toString());
            resultMessage.setMessageId(commandMessage.getMessageId());
            resultMessage.setHostname(commandMessage.getHostname());
            try {
                session.sendMessage(new BinaryMessage(serializer.serialize(resultMessage)));
            } catch (IOException e) {
                log.error(MessageFormat.format("Error sending resultMessage to server: {0}", e.getMessage()));
            }

        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        executor.scheduleAtFixedRate(() -> {
            try {
                HeartbeatMessage heartbeatMessage = new HeartbeatMessage();
                heartbeatMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
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
