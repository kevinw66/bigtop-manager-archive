package org.apache.bigtop.manager.stack.core.executor;

import org.apache.bigtop.manager.common.message.type.CommandMessage;

public interface Executor {
    Object execute(CommandMessage commandMessage);
}
