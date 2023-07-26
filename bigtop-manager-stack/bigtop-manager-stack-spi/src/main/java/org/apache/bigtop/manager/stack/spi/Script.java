package org.apache.bigtop.manager.stack.spi;

import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.commons.lang3.StringUtils;

public interface Script extends SPIIdentify {
    ShellResult install();

    ShellResult configuration();

    default ShellResult start() {
        return null;
    }

    default ShellResult stop() {
        return null;
    }

    default ShellResult restart() {
        ShellResult shellResult = stop();
        if (shellResult.getExitCode() != 0) {
            return shellResult;
        }
        ShellResult shellResult1 = start();
        if (shellResult1.getExitCode() != 0) {
            return shellResult1;
        }

        return new ShellResult(0,
                StringUtils.join(shellResult.getOutput(), shellResult1.getOutput()),
                StringUtils.join(shellResult.getErrMsg(), shellResult1.getErrMsg()));
    }

    ShellResult status();
}
