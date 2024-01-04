package org.apache.bigtop.manager.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Command {

    INSTALL,

    REINSTALL,

    UNINSTALL,

    START,

    STOP,

    STATUS,

    RESTART,

    CONFIGURATION,

    CHECK,

    CUSTOM_COMMAND,
    ;

    @JsonCreator
    public static Command fromString(String value) {
        return Command.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toLowerCase() {
        return name().toLowerCase();
    }
}
