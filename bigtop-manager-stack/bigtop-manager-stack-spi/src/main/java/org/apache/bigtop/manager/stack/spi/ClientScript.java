package org.apache.bigtop.manager.stack.spi;

import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;

public interface ClientScript extends Script {

    default ShellResult start(BaseParams baseParams) {
        configuration(baseParams);
        return DefaultShellResult.success();
    }

    default ShellResult stop(BaseParams baseParams) {
        return DefaultShellResult.success();
    }

    default ShellResult status(BaseParams baseParams) {
        return DefaultShellResult.success();
    }
}
