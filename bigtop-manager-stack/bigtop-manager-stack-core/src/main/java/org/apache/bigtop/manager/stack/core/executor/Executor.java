package org.apache.bigtop.manager.stack.core.executor;

import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;

public interface Executor {
    ShellResult execute(CommandPayload commandPayload);
}
