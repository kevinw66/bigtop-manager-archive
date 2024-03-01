package org.apache.bigtop.manager.agent.executor;

import org.apache.bigtop.manager.common.message.entity.command.CommandMessageType;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;

/**
 * Interface for executing commands on agent.
 */
public interface CommandExecutor {

    /**
     * Get the type of the command message.
     * @return CommandMessageType - the type of the command message.
     */
    CommandMessageType getCommandMessageType();

    /**
     * Execute the command.
     * @param message - the message for command that needs to be executed.
     */
    void execute(CommandRequestMessage message);
}
