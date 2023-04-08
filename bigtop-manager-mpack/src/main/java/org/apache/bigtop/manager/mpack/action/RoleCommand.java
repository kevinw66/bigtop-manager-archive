package org.apache.bigtop.manager.mpack.action;


public enum RoleCommand {

    /*
     * When adding/modifying enum members, please beware that except Java usages,
     * RoleCommand string representations are used at role_command_order.json
     * files
     */

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

