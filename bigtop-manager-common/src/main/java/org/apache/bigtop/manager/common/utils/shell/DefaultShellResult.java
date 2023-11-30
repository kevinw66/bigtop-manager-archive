package org.apache.bigtop.manager.common.utils.shell;

import org.apache.bigtop.manager.common.constants.MessageConstants;

public class DefaultShellResult {

    public static final ShellResult FAIL = new ShellResult(MessageConstants.DEFAULT_FAIL_CODE, "Default failed!!!", "");

    public static ShellResult success(String output) {
        return new ShellResult(MessageConstants.DEFAULT_FAIL_CODE, output, "");
    }

    public static ShellResult success() {
        return success("Default Successful!!!");
    }
}
