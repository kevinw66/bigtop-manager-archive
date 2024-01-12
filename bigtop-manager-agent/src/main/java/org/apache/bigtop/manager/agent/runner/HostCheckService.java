package org.apache.bigtop.manager.agent.runner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.ws.AgentWsTools;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.message.type.HostCheckPayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;
import org.apache.bigtop.manager.common.utils.Environments;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.os.TimeSyncDetection;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.common.utils.thread.BaseDaemonThread;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class HostCheckService {

    /**
     * attemptQueue
     */
    private final BlockingQueue<RequestMessage> eventQueue = new LinkedBlockingQueue<>();

    /**
     * task event worker
     */
    private Thread taskEventThread;

    @Value("${bigtop.manager.dev-mode}")
    private Boolean devMode;

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
     * @param requestMessage hostCheckContext
     */
    public void addEvent(RequestMessage requestMessage) {
        eventQueue.add(requestMessage);
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
                    RequestMessage context = eventQueue.take();
                    executeTask(context);
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
        HostCheckPayload hostCheckMessage = JsonUtils.readFromString(requestMessage.getMessagePayload(), HostCheckPayload.class);

        HostCheckType[] hostCheckTypes = hostCheckMessage.getHostCheckTypes();
        ResultMessage resultMessage = new ResultMessage();

        if (!Environments.isDevMode()) {
            for (HostCheckType hostCheckType : hostCheckTypes) {
                switch (hostCheckType) {
                    case TIME_SYNC -> {
                        ShellResult shellResult = TimeSyncDetection.checkTimeSync();

                        resultMessage.setCode(shellResult.getExitCode());
                        resultMessage.setResult(shellResult.getResult());

                    }
                    default -> log.warn("unknown hostCheckType");
                }
            }
        } else {
            resultMessage.setCode(MessageConstants.SUCCESS_CODE);
            resultMessage.setResult("Success on dev mode");
        }

        resultMessage.setMessageId(requestMessage.getMessageId());
        resultMessage.setHostname(requestMessage.getHostname());
        resultMessage.setMessageType(requestMessage.getMessageType());
        resultMessage.setJobId(requestMessage.getJobId());
        resultMessage.setStageId(requestMessage.getStageId());
        resultMessage.setTaskId(requestMessage.getTaskId());

        agentWsTools.sendMessage(resultMessage);
    }
}
