package org.apache.bigtop.manager.agent.scheduler;

import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;

/**
 * Interface for scheduling commands on agent.
 */
public interface CommandScheduler {

    /**
     * Submit a command request message to the scheduler.
     * @param message - the command request message that needs to be submitted.
     */
    void submit(CommandRequestMessage message);

    /**
     * Start the command scheduler.
     */
    void start();

    /**
     * Stop the command scheduler.
     */
    void stop();
}
