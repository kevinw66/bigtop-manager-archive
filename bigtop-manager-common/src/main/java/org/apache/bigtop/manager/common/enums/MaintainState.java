package org.apache.bigtop.manager.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.bigtop.manager.common.utils.CaseUtils;

@Getter
public enum MaintainState {
    UNINSTALLED,

    INSTALLED,

    MAINTAINED,

    STARTED,

    STOPPED,
    ;

    @JsonCreator
    public static MaintainState fromString(String value) {
        return MaintainState.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toCamelCase() {
        return CaseUtils.toCamelCase(name());
    }
}
