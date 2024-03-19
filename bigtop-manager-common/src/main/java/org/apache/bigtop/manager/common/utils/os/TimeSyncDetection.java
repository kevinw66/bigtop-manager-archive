package org.apache.bigtop.manager.common.utils.os;

import org.apache.bigtop.manager.common.shell.ShellExecutor;
import org.apache.bigtop.manager.common.shell.ShellResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimeSyncDetection {

    public static ShellResult checkTimeSync() {
        List<String> params = new ArrayList<>();
        params.add("systemctl");
        params.add("status");
        params.add("chronyd");
        ShellResult shellResult;
        try {
            shellResult = ShellExecutor.execCommand(params);

            if (shellResult.getExitCode() != 0) {
                params.remove(params.size() - 1);
                params.add("ntpd");
                shellResult = ShellExecutor.execCommand(params);
            }

        } catch (IOException e) {
            shellResult = new ShellResult();
            shellResult.setExitCode(-1);
            shellResult.setErrMsg("Neither chronyd nor ntpd check failed.");
        }

        return shellResult;
    }
}
