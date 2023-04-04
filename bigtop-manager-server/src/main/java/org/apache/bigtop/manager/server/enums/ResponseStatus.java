package org.apache.bigtop.manager.server.enums;

public enum ResponseStatus {

    SUCCESS(0, "Success"),

    INTERNAL_SERVER_ERROR(-1, "Internal Server Error"),

    AAA(10000, "aaa"),
    ;

    private final Integer code;

    private final String message;

    ResponseStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
