package org.apache.bigtop.manager.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.bigtop.manager.common.utils.CaseUtils;

public enum Command {

    CREATE,

    INSTALL,

    UNINSTALL,

    START,

    STOP,

    STATUS,

    RESTART,

    CONFIGURE,

    CHECK,

    CUSTOM,
    ;

    @JsonCreator
    public static Command fromString(String value) {
        return Command.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toCamelCase() {
        return CaseUtils.toCamelCase(name());
    }
}
