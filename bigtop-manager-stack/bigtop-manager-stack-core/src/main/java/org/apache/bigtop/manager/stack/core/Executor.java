package org.apache.bigtop.manager.stack.core;

import org.apache.bigtop.manager.common.message.type.CommandMessage;

public interface Executor {
    Object execute(CommandMessage commandMessage);
}
