package org.apache.bigtop.manager.stack.core;

import org.apache.bigtop.manager.common.message.type.CommandMessage;

public interface Execute {
    void execute(CommandMessage commandMessage);
}
