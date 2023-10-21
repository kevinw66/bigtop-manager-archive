package org.apache.bigtop.manager.agent.runner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.ws.AgentWebSocketHandler;
import org.apache.bigtop.manager.agent.ws.AgentWsTools;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.common.utils.thread.BaseDaemonThread;
import org.apache.bigtop.manager.stack.core.executor.Executor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

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
    private final BlockingQueue<CommandContext> eventQueue = new LinkedBlockingQueue<>();

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
                List<CommandContext> remainEvents = new ArrayList<>(eventQueue.size());
                eventQueue.drainTo(remainEvents);
                for (CommandContext commandContext : remainEvents) {
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
    public void addEvent(CommandContext context) {
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
                    CommandContext context = eventQueue.take();
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
     * @param context {@link CommandContext}
     */
    public void executeTask(CommandContext context) {
        CommandMessage commandMessage = context.getCommandMessage();
        log.info("[agent executeTask] taskEvent is: {}", context);
        Object result = stackExecutor.execute(commandMessage);

        if (result instanceof ShellResult shellResult) {
            ResultMessage resultMessage = new ResultMessage();
            resultMessage.setCode(shellResult.getExitCode());
            resultMessage.setResult(shellResult.getResult());
            resultMessage.setMessageId(commandMessage.getMessageId());
            resultMessage.setHostname(commandMessage.getHostname());
            resultMessage.setMessageType(MessageType.COMMAND);

            resultMessage.setJobId(commandMessage.getJobId());
            resultMessage.setStageId(commandMessage.getStageId());
            resultMessage.setTaskId(commandMessage.getTaskId());

            agentWsTools.sendMessage(resultMessage);
        }
    }
}
