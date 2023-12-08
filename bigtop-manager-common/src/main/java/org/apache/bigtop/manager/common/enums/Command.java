package org.apache.bigtop.manager.common.enums;

public enum Command {

    INSTALL,

    /*
    Actually, it is the packaging of INSTALL, which will send the INSTALL command to the agent
    */
    REINSTALL,

    UNINSTALL,

    START,

    STOP,

    STATUS,

    RESTART,

    CONFIGURATION,

    CHECK,

    CUSTOM_COMMAND,
}
