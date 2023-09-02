package org.apache.bigtop.manager.server.enums;

import lombok.Getter;

@Getter
public enum ResponseStatus {

    SUCCESS(0, "Success"),

    INTERNAL_SERVER_ERROR(-1, "Internal Server Error"),
    ;

    private final Integer code;

    private final String message;

    ResponseStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
