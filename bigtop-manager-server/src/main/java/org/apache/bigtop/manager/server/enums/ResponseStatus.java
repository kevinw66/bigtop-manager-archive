package org.apache.bigtop.manager.server.enums;

import lombok.Getter;
import org.apache.bigtop.manager.server.utils.MessageSourceUtils;

@Getter
public enum ResponseStatus {

    SUCCESS(0, LocaleKeys.REQUEST_SUCCESS),

    INTERNAL_SERVER_ERROR(-1, LocaleKeys.REQUEST_FAILED),

    PARAMETER_ERROR(1, LocaleKeys.PARAMETER_ERROR),

    ;

    private final Integer code;

    private final LocaleKeys key;

    ResponseStatus(Integer code, LocaleKeys key) {
        this.code = code;
        this.key = key;
    }

    public String getMessage() {
        return MessageSourceUtils.getMessage(key);
    }
}
