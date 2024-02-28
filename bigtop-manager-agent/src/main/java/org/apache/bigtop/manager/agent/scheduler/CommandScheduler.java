package org.apache.bigtop.manager.agent.scheduler;

import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;

public interface CommandScheduler {

    void submit(CommandRequestMessage message);

    void start();

    void stop();
}
