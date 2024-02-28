package org.apache.bigtop.manager.agent.executor;

import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;

public interface CommandExecutor {

    MessageType getMessageType();

    void execute(CommandRequestMessage message);
}
