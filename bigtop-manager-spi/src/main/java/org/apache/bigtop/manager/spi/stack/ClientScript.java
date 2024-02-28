package org.apache.bigtop.manager.spi.stack;

import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;

public interface ClientScript extends Script {

    default ShellResult start(Params params) {
        configure(params);
        return DefaultShellResult.success();
    }

    default ShellResult stop(Params params) {
        return DefaultShellResult.success();
    }

    default ShellResult status(Params params) {
        return DefaultShellResult.success();
    }
}
