package org.apache.bigtop.manager.stack.spi;

import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.commons.lang3.StringUtils;

public interface Script extends SPIIdentify {

    ShellResult install(BaseParams baseParams);

    ShellResult configure(BaseParams baseParams);

    ShellResult start(BaseParams baseParams);

    ShellResult stop(BaseParams baseParams);

    default ShellResult restart(BaseParams baseParams) {
        ShellResult shellResult = stop(baseParams);
        if (shellResult.getExitCode() != 0) {
            return shellResult;
        }
        ShellResult shellResult1 = start(baseParams);
        if (shellResult1.getExitCode() != 0) {
            return shellResult1;
        }

        return new ShellResult(0,
                StringUtils.join(shellResult.getOutput(), shellResult1.getOutput()),
                StringUtils.join(shellResult.getErrMsg(), shellResult1.getErrMsg()));
    }

    ShellResult status(BaseParams baseParams);

    default ShellResult check(BaseParams baseParams) {
        return DefaultShellResult.success();
    }
}
