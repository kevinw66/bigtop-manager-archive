package org.apache.bigtop.manager.stack.core.executor;

import org.apache.bigtop.manager.common.message.type.CommandPayload;

public interface Executor {
    Object execute(CommandPayload commandPayload);
}
