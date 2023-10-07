package org.apache.bigtop.manager.agent.runner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.ws.AgentWsTools;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.HostCacheMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.thread.BaseDaemonThread;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxFileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.apache.bigtop.manager.common.constants.HostCacheConstants.*;

@Slf4j
@Component
public class HostCacheService {

    /**
     * attemptQueue
     */
    private final BlockingQueue<HostCacheContext> eventQueue = new LinkedBlockingQueue<>();

    /**
     * task event worker
     */
    private Thread taskEventThread;

    @PostConstruct
    public void start() {
        this.taskEventThread = new HostCacheDispatchThread();
        log.info("TaskEvent dispatch thread starting");
        this.taskEventThread.start();
        log.info("TaskEvent dispatch thread started");
    }

    @PreDestroy
    public void stop() {
        try {
            this.taskEventThread.interrupt();
            if (!eventQueue.isEmpty()) {
                List<HostCacheContext> remainEvents = new ArrayList<>(eventQueue.size());
                eventQueue.drainTo(remainEvents);
                for (HostCacheContext context : remainEvents) {
                    executeTask(context);
                }
            }
        } catch (Exception e) {
            log.error("TaskEventService stop error:", e);
        }
    }


    /**
     * add event
     *
     * @param context hostCacheEvent
     */
    public void addEvent(HostCacheContext context) {
        eventQueue.add(context);
    }

    @Resource
    private AgentWsTools agentWsTools;

    /**
     * Dispatch event to target task runnable.
     */
    class HostCacheDispatchThread extends BaseDaemonThread {

        protected HostCacheDispatchThread() {
            super("HostCacheThread");
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // if not task event, blocking here
                    HostCacheContext hostCacheContext = eventQueue.take();
                    executeTask(hostCacheContext);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("persist task error", e);
                }
            }
        }
    }

    /**
     * execute task
     * @param context {@link HostCacheContext}
     */
    public void executeTask(HostCacheContext context) {
        WebSocketSession session = context.getSession();
        HostCacheMessage hostCacheMessage = context.getHostCacheMessage();
        log.info("[agent executeTask] taskEvent is: {}", context);
        String cacheDir = Constants.STACK_CACHE_DIR;

        LinuxFileUtils.createDirectories(cacheDir, "root", "root", "rwxr-xr-x", false);

        try {
            JsonUtils.writeToFile(cacheDir + SETTINGS_INFO, hostCacheMessage.getSettings());
            JsonUtils.writeToFile(cacheDir + CONFIGURATIONS_INFO, hostCacheMessage.getConfigurations());
            JsonUtils.writeToFile(cacheDir + HOSTS_INFO, hostCacheMessage.getClusterHostInfo());
            JsonUtils.writeToFile(cacheDir + USERS_INFO, hostCacheMessage.getUserInfo());
        } catch (Exception e) {
            log.warn(" [{}|{}|{}|{}] cache error: ", SETTINGS_INFO, CONFIGURATIONS_INFO, HOSTS_INFO, USERS_INFO, e);
        }
        JsonUtils.writeToFile(cacheDir + REPOS_INFO, hostCacheMessage.getRepoInfo());
        JsonUtils.writeToFile(cacheDir + CLUSTER_INFO, hostCacheMessage.getClusterInfo());

        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setMessageId(hostCacheMessage.getMessageId());
        resultMessage.setHostname(hostCacheMessage.getHostname());
        resultMessage.setCode(MessageConstants.SUCCESS_CODE);
        resultMessage.setResult(MessageFormat.format("Host [{0}] cached successful!!!", hostCacheMessage.getHostname()));
        resultMessage.setMessageType(MessageType.HOST_CACHE);

        agentWsTools.sendMessage(session, resultMessage);
    }
}
