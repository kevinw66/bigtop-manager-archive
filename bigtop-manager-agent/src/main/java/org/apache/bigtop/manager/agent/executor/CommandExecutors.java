package org.apache.bigtop.manager.agent.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.holder.SpringContextHolder;
import org.apache.bigtop.manager.common.enums.MessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class CommandExecutors {

    private static final AtomicBoolean LOADED = new AtomicBoolean(false);

    private static final Map<MessageType, String> COMMAND_EXECUTORS = new HashMap<>();

    public static CommandExecutor getCommandExecutor(MessageType messageType) {
        if (!LOADED.get()) {
            load();
        }

        String beanName = COMMAND_EXECUTORS.get(messageType);
        return SpringContextHolder.getApplicationContext().getBean(beanName, CommandExecutor.class);
    }

    private static synchronized void load() {
        if (LOADED.get()) {
            return;
        }

        for (Map.Entry<String, CommandExecutor> entry : SpringContextHolder.getCommandExecutors().entrySet()) {
            String beanName = entry.getKey();
            CommandExecutor commandExecutor = entry.getValue();
            if (COMMAND_EXECUTORS.containsKey(commandExecutor.getMessageType())) {
                log.error("Duplicate CommandExecutor with message type: {}", commandExecutor.getMessageType());
                continue;
            }

            COMMAND_EXECUTORS.put(commandExecutor.getMessageType(), beanName);
            log.info("Load JobRunner: {} with identifier: {}", commandExecutor.getClass().getName(), commandExecutor.getMessageType());
        }

        LOADED.set(true);
    }
}
