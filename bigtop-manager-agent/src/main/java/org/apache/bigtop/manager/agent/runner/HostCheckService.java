package org.apache.bigtop.manager.agent.runner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.serializer.MessageSerializer;
import org.apache.bigtop.manager.common.message.type.HostCheckMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;
import org.apache.bigtop.manager.common.utils.os.TimeSyncDetection;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.common.utils.thread.BaseDaemonThread;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.text.MessageFormat;
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
    private final BlockingQueue<HostCheckContext> eventQueue = new LinkedBlockingQueue<>();

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
                List<HostCheckContext> remainEvents = new ArrayList<>(eventQueue.size());
                eventQueue.drainTo(remainEvents);
                for (HostCheckContext context : remainEvents) {
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
     * @param context hostCheckContext
     */
    public void addEvent(HostCheckContext context) {
        eventQueue.add(context);
    }

    @Resource
    private MessageSerializer serializer;

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
                    HostCheckContext context = eventQueue.take();
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
     * @param context {@link HostCheckContext}
     */
    public void executeTask(HostCheckContext context) {
        WebSocketSession session = context.getSession();
        HostCheckMessage hostCheckMessage = context.getHostCheckMessage();

        HostCheckType[] hostCheckTypes = hostCheckMessage.getHostCheckTypes();

        for (HostCheckType hostCheckType : hostCheckTypes) {
            switch (hostCheckType) {
                case TIME_SYNC -> {
                    ResultMessage resultMessage = new ResultMessage();
                    ShellResult shellResult = TimeSyncDetection.checkTimeSync();

                    resultMessage.setCode(shellResult.getExitCode());
                    resultMessage.setResult(shellResult.toString());
                    resultMessage.setMessageId(hostCheckMessage.getMessageId());
                    resultMessage.setHostname(hostCheckMessage.getHostname());
                    resultMessage.setMessageType(MessageType.HOST_CHECK);

                    try {
                        session.sendMessage(new BinaryMessage(serializer.serialize(resultMessage)));
                    } catch (IOException e) {
                        log.error(MessageFormat.format("Error sending resultMessage to server: {0}", e.getMessage()));
                    }
                }
                default -> log.warn("unknown hostCheckType");

            }
        }
    }
}
