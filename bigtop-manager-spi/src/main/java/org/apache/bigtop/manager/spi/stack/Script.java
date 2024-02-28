package org.apache.bigtop.manager.spi.stack;

import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.spi.plugin.PrioritySPI;
import org.apache.commons.lang3.StringUtils;

public interface Script extends PrioritySPI {

    ShellResult install(Params params);

    ShellResult configure(Params params);

    ShellResult start(Params params);

    ShellResult stop(Params params);

    default ShellResult restart(Params params) {
        ShellResult shellResult = stop(params);
        if (shellResult.getExitCode() != 0) {
            return shellResult;
        }
        ShellResult shellResult1 = start(params);
        if (shellResult1.getExitCode() != 0) {
            return shellResult1;
        }

        return new ShellResult(0,
                StringUtils.join(shellResult.getOutput(), shellResult1.getOutput()),
                StringUtils.join(shellResult.getErrMsg(), shellResult1.getErrMsg()));
    }

    ShellResult status(Params params);

    default ShellResult check(Params params) {
        return DefaultShellResult.success();
    }
}