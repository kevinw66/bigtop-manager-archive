package org.apache.bigtop.manager.agent.runner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.ws.AgentWsTools;
import org.apache.bigtop.manager.common.message.entity.payload.CommandPayload;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;
import org.apache.bigtop.manager.common.message.entity.command.CommandResponseMessage;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
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
    private final BlockingQueue<CommandRequestMessage> eventQueue = new LinkedBlockingQueue<>();

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
                List<CommandRequestMessage> remainEvents = new ArrayList<>(eventQueue.size());
                eventQueue.drainTo(remainEvents);
                for (CommandRequestMessage commandContext : remainEvents) {
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
    public void addEvent(CommandRequestMessage context) {
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
                    CommandRequestMessage context = eventQueue.take();
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
     * @param commandRequestMessage {@link CommandRequestMessage}
     */
    public void executeTask(CommandRequestMessage commandRequestMessage) {
        CommandResponseMessage commandResponseMessage = new CommandResponseMessage();
        commandResponseMessage.setMessageId(commandRequestMessage.getMessageId());
        commandResponseMessage.setHostname(commandRequestMessage.getHostname());
        commandResponseMessage.setMessageType(commandRequestMessage.getMessageType());

        commandResponseMessage.setJobId(commandRequestMessage.getJobId());
        commandResponseMessage.setStageId(commandRequestMessage.getStageId());
        commandResponseMessage.setTaskId(commandRequestMessage.getTaskId());
        try {
            CommandPayload commandPayload = JsonUtils.readFromString(commandRequestMessage.getMessagePayload(), CommandPayload.class);
            log.info("[agent executeTask] taskEvent is: {}", commandRequestMessage);
            ShellResult shellResult = stackExecutor.execute(commandPayload);

            commandResponseMessage.setCode(shellResult.getExitCode());
            commandResponseMessage.setResult(shellResult.getResult());
            agentWsTools.sendMessage(commandResponseMessage);
        } catch (Exception e) {
            log.error("execute error: ", e);
            ShellResult fail = DefaultShellResult.FAIL;
            commandResponseMessage.setCode(fail.getExitCode());
            commandResponseMessage.setResult(fail.getResult());
            agentWsTools.sendMessage(commandResponseMessage);
        }
    }
}
