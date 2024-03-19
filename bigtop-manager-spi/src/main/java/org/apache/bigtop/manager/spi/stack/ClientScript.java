package org.apache.bigtop.manager.spi.stack;

import org.apache.bigtop.manager.common.shell.ShellResult;

public interface ClientScript extends Script {

    default ShellResult start(Params params) {
        configure(params);
        return ShellResult.success();
    }

    default ShellResult stop(Params params) {
        return ShellResult.success();
    }

    default ShellResult status(Params params) {
        return ShellResult.success();
    }
}
