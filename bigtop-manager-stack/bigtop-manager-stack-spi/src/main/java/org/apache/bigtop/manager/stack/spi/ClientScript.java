package org.apache.bigtop.manager.stack.spi;

import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;

public interface ClientScript extends Script {

    default ShellResult start(CommandPayload commandMessage) {
        configuration(commandMessage);
        return DefaultShellResult.SUCCESS;
    }

    default ShellResult stop(CommandPayload commandMessage) {
        return DefaultShellResult.SUCCESS;
    }

    default ShellResult status(CommandPayload commandMessage) {
        return DefaultShellResult.SUCCESS;
    }
}
