package org.apache.bigtop.manager.agent.runner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.ws.AgentWsTools;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.message.entity.payload.HostCheckPayload;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;
import org.apache.bigtop.manager.common.message.entity.command.CommandResponseMessage;
import org.apache.bigtop.manager.common.message.entity.pojo.HostCheckType;
import org.apache.bigtop.manager.common.utils.Environments;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.os.TimeSyncDetection;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.common.utils.thread.BaseDaemonThread;
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
    private final BlockingQueue<CommandRequestMessage> eventQueue = new LinkedBlockingQueue<>();

    /**
     * task event worker
     */
    private Thread taskEventThread;

    @PostConstruct
    public void start() {
        this.taskEventThread = new HostCheckDispatchThread();
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
     *
     * @param commandRequestMessage hostCheckContext
     */
    public void addEvent(CommandRequestMessage commandRequestMessage) {
        eventQueue.add(commandRequestMessage);
    }

    @Resource
    private AgentWsTools agentWsTools;

    /**
     * Dispatch event to target task runnable.
     */
    class HostCheckDispatchThread extends BaseDaemonThread {

        protected HostCheckDispatchThread() {
            super("HostCheckThread");
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
        HostCheckPayload hostCheckMessage = JsonUtils.readFromString(commandRequestMessage.getMessagePayload(), HostCheckPayload.class);

        HostCheckType[] hostCheckTypes = hostCheckMessage.getHostCheckTypes();
        CommandResponseMessage commandResponseMessage = new CommandResponseMessage();

        if (!Environments.isDevMode()) {
            for (HostCheckType hostCheckType : hostCheckTypes) {
                switch (hostCheckType) {
                    case TIME_SYNC -> {
                        ShellResult shellResult = TimeSyncDetection.checkTimeSync();

                        commandResponseMessage.setCode(shellResult.getExitCode());
                        commandResponseMessage.setResult(shellResult.getResult());

                    }
                    default -> log.warn("unknown hostCheckType");
                }
            }
        } else {
            commandResponseMessage.setCode(MessageConstants.SUCCESS_CODE);
            commandResponseMessage.setResult("Success on dev mode");
        }

        commandResponseMessage.setMessageId(commandRequestMessage.getMessageId());
        commandResponseMessage.setHostname(commandRequestMessage.getHostname());
        commandResponseMessage.setMessageType(commandRequestMessage.getMessageType());
        commandResponseMessage.setJobId(commandRequestMessage.getJobId());
        commandResponseMessage.setStageId(commandRequestMessage.getStageId());
        commandResponseMessage.setTaskId(commandRequestMessage.getTaskId());

        agentWsTools.sendMessage(commandResponseMessage);
    }
}
