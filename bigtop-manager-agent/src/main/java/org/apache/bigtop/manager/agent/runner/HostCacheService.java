package org.apache.bigtop.manager.agent.runner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.ws.AgentWsTools;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.message.type.HostCachePayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.utils.Environments;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.thread.BaseDaemonThread;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxFileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private final BlockingQueue<RequestMessage> eventQueue = new LinkedBlockingQueue<>();

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
                List<RequestMessage> remainEvents = new ArrayList<>(eventQueue.size());
                eventQueue.drainTo(remainEvents);
                for (RequestMessage context : remainEvents) {
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
    public void addEvent(RequestMessage context) {
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
                    RequestMessage hostCacheContext = eventQueue.take();
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
     *
     * @param requestMessage {@link RequestMessage}
     */
    public void executeTask(RequestMessage requestMessage) {
        HostCachePayload hostCachePayload = JsonUtils.readFromString(requestMessage.getMessagePayload(), HostCachePayload.class);
        log.info("[agent executeTask] taskEvent is: {}", requestMessage);
        String cacheDir = Constants.STACK_CACHE_DIR;

        if (!Environments.isDevMode()) {
            LinuxFileUtils.createDirectories(cacheDir, "root", "root", "rwxr-xr-x", false);

            JsonUtils.writeToFile(cacheDir + SETTINGS_INFO, hostCachePayload.getSettings());
            JsonUtils.writeToFile(cacheDir + CONFIGURATIONS_INFO, hostCachePayload.getConfigurations());
            JsonUtils.writeToFile(cacheDir + HOSTS_INFO, hostCachePayload.getClusterHostInfo());
            JsonUtils.writeToFile(cacheDir + USERS_INFO, hostCachePayload.getUserInfo());
            JsonUtils.writeToFile(cacheDir + COMPONENTS_INFO, hostCachePayload.getComponentInfo());
            JsonUtils.writeToFile(cacheDir + REPOS_INFO, hostCachePayload.getRepoInfo());
            JsonUtils.writeToFile(cacheDir + CLUSTER_INFO, hostCachePayload.getClusterInfo());
        }

        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setCode(MessageConstants.SUCCESS_CODE);
        resultMessage.setResult(MessageFormat.format("Host [{0}] cached successful!!!", requestMessage.getHostname()));

        resultMessage.setMessageType(requestMessage.getMessageType());
        resultMessage.setMessageId(requestMessage.getMessageId());
        resultMessage.setHostname(requestMessage.getHostname());
        resultMessage.setTaskId(requestMessage.getTaskId());
        resultMessage.setStageId(requestMessage.getStageId());
        resultMessage.setJobId(requestMessage.getJobId());

        agentWsTools.sendMessage(resultMessage);
    }
}
