package org.apache.bigtop.manager.server.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CommandLevel {

    /**
     * cluster command
     */
    CLUSTER,

    /**
     * service command
     */
    SERVICE,

    /**
     * component command
     */
    COMPONENT,

    /**
     * host command
     */
    HOST,


    INTERNAL_SERVICE_INSTALL,
    ;

    @JsonCreator
    public static CommandLevel fromString(String value) {
        return CommandLevel.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toLowerCase() {
        return name().toLowerCase();
    }
}
