package org.apache.bigtop.manager.mpack.action;


public enum RoleCommand {

    INSTALL,
    UNINSTALL,
    START,
    RESTART,
    STOP,
    EXECUTE,
    ABORT,
    UPGRADE,
    SERVICE_CHECK,

    /**
     * Represents any custom command
     */
    CUSTOM_COMMAND,

    /**
     * Represents any action
     */
    ACTIONEXECUTE
}

