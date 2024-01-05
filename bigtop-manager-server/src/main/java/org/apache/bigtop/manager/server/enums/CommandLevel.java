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
