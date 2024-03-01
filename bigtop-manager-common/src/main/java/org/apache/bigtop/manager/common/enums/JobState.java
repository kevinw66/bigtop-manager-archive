package org.apache.bigtop.manager.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.bigtop.manager.common.utils.CaseUtils;

public enum JobState {

    PENDING,

    PROCESSING,

    SUCCESSFUL,

    FAILED,

    CANCELED,
    ;

    @JsonCreator
    public static JobState fromString(String value) {
        return JobState.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toCamelCase() {
        return CaseUtils.toCamelCase(name());
    }
}
