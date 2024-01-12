package org.apache.bigtop.manager.common.utils;

import org.apache.commons.lang3.StringUtils;

public class Environments {

    /**
     * Indicates whether the application is running in development mode, which is disabled by default.
     * In development mode, only NOP stacks are available and no real shell commands will be executed on the agent side.
     */
    public static Boolean isDevMode() {
        String devMode = System.getenv("DEV_MODE");
        return StringUtils.isNotBlank(devMode) && devMode.equals("true");
    }
}
