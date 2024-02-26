package org.apache.bigtop.manager.agent.runner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.ws.AgentWsTools;
import org.apache.bigtop.manager.common.constants.Constants;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.message.entity.payload.CacheMessagePayload;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;
import org.apache.bigtop.manager.common.message.entity.command.CommandResponseMessage;
import org.apache.bigtop.manager.common.utils.Environments;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.thread.BaseDaemonThread;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxFileUtils;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.apache.bigtop.manager.common.constants.CacheFiles.*;

@Slf4j
@Component
public class CacheDistributeService {

    /**
     * attemptQueue
     */
    private final BlockingQueue<CommandRequestMessage> eventQueue = new LinkedBlockingQueue<>();

    /**
     * task event worker
     */
    private Thread taskEventThread;

    @PostConstruct
    public void start() {
        this.taskEventThread = new CacheDistributeDispatchThread();
        log.info("TaskEvent dispatch thread starting");
        this.taskEventThread.start();
        log.info("TaskEvent dispatch thread started");
    }

    @PreDestroy
    public void stop() {
        try {
            this.taskEventThread.interrupt();
            if (!eventQueue.isEmpty()) {
                List<CommandRequestMessage> remainEvents = new ArrayList<>(eventQueue.size());
                eventQueue.drainTo(remainEvents);
                for (CommandRequestMessage context : remainEvents) {
                    executeTask(context);
                }
            }
        } catch (Exception e) {
            log.error("TaskEventService stop error:", e);
        }
    }


    /**
     * add event
     */
    public void addEvent(CommandRequestMessage context) {
        eventQueue.add(context);
    }

    @Resource
    private AgentWsTools agentWsTools;

    /**
     * Dispatch event to target task runnable.
     */
    class CacheDistributeDispatchThread extends BaseDaemonThread {

        protected CacheDistributeDispatchThread() {
            super("CacheDistributeThread");
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // if not task event, blocking here
                    CommandRequestMessage message = eventQueue.take();
                    executeTask(message);
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
     * @param commandRequestMessage {@link CommandRequestMessage}
     */
    public void executeTask(CommandRequestMessage commandRequestMessage) {
        CacheMessagePayload cacheMessagePayload = JsonUtils.readFromString(commandRequestMessage.getMessagePayload(), CacheMessagePayload.class);
        log.info("[agent executeTask] taskEvent is: {}", commandRequestMessage);
        String cacheDir = Constants.STACK_CACHE_DIR;

        if (!Environments.isDevMode()) {
            LinuxFileUtils.createDirectories(cacheDir, "root", "root", "rwxr-xr-x", false);

            JsonUtils.writeToFile(cacheDir + SETTINGS_INFO, cacheMessagePayload.getSettings());
            JsonUtils.writeToFile(cacheDir + CONFIGURATIONS_INFO, cacheMessagePayload.getConfigurations());
            JsonUtils.writeToFile(cacheDir + HOSTS_INFO, cacheMessagePayload.getClusterHostInfo());
            JsonUtils.writeToFile(cacheDir + USERS_INFO, cacheMessagePayload.getUserInfo());
            JsonUtils.writeToFile(cacheDir + COMPONENTS_INFO, cacheMessagePayload.getComponentInfo());
            JsonUtils.writeToFile(cacheDir + REPOS_INFO, cacheMessagePayload.getRepoInfo());
            JsonUtils.writeToFile(cacheDir + CLUSTER_INFO, cacheMessagePayload.getClusterInfo());
        }

        CommandResponseMessage commandResponseMessage = new CommandResponseMessage();
        commandResponseMessage.setCode(MessageConstants.SUCCESS_CODE);
        commandResponseMessage.setResult(MessageFormat.format("Host [{0}] cached successful!!!", commandRequestMessage.getHostname()));

        commandResponseMessage.setMessageType(commandRequestMessage.getMessageType());
        commandResponseMessage.setMessageId(commandRequestMessage.getMessageId());
        commandResponseMessage.setHostname(commandRequestMessage.getHostname());
        commandResponseMessage.setTaskId(commandRequestMessage.getTaskId());
        commandResponseMessage.setStageId(commandRequestMessage.getStageId());
        commandResponseMessage.setJobId(commandRequestMessage.getJobId());

        agentWsTools.sendMessage(commandResponseMessage);
    }
}
