package org.apache.bigtop.manager.common.utils.shell;

import org.apache.bigtop.manager.common.constants.MessageConstants;

public class DefaultShellResult {

    public static final ShellResult SUCCESS = new ShellResult(MessageConstants.SUCCESS_CODE, "Default Successful!!!", "");

    public static final ShellResult FAIL = new ShellResult(MessageConstants.DEFAULT_FAIL_CODE, "Default failed!!!", "");
}
