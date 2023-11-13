package org.apache.bigtop.manager.agent.runner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.ws.AgentWsTools;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.common.utils.thread.BaseDaemonThread;
import org.apache.bigtop.manager.stack.core.executor.Executor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class CommandService {

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
        this.taskEventThread = new CommandDispatchThread();
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
                for (RequestMessage commandContext : remainEvents) {
                    executeTask(commandContext);
                }
            }
        } catch (Exception e) {
            log.error("TaskEventService stop error:", e);
        }
    }


    /**
     * add event
     *
     * @param context commandContext
     */
    public void addEvent(RequestMessage context) {
        eventQueue.add(context);
    }

    @Resource
    private Executor stackExecutor;

    @Resource
    private AgentWsTools agentWsTools;

    /**
     * Dispatch event to target task runnable.
     */
    class CommandDispatchThread extends BaseDaemonThread {

        protected CommandDispatchThread() {
            super("CommandThread");
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
        CommandPayload commandMessage = JsonUtils.readFromString(requestMessage.getMessagePayload(), CommandPayload.class);
        log.info("[agent executeTask] taskEvent is: {}", requestMessage);
        Object result = stackExecutor.execute(commandMessage);

        if (result instanceof ShellResult shellResult) {
            ResultMessage resultMessage = new ResultMessage();
            resultMessage.setCode(shellResult.getExitCode());
            resultMessage.setResult(shellResult.getResult());

            resultMessage.setMessageId(requestMessage.getMessageId());
            resultMessage.setHostname(requestMessage.getHostname());
            resultMessage.setMessageType(requestMessage.getMessageType());

            resultMessage.setJobId(requestMessage.getJobId());
            resultMessage.setStageId(requestMessage.getStageId());
            resultMessage.setTaskId(requestMessage.getTaskId());

            agentWsTools.sendMessage(resultMessage);
        }
    }
}
