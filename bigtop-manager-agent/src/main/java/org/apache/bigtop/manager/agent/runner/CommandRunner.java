package org.apache.bigtop.manager.agent.runner;

import org.apache.bigtop.manager.common.message.type.CommandMessage;

public interface CommandRunner {

    void run(CommandMessage commandMessage);
}
