package org.apache.bigtop.manager.server.enums;

public enum ServerExceptionStatus {

    NEED_LOGIN(10000, "Not logged in"),
    USERNAME_OR_PASSWORD_REQUIRED(10001, "Username or password should not be empty"),
    INCORRECT_USERNAME_OR_PASSWORD(10002, "Incorrect username or password"),
    ;

    private final Integer code;

    private final String message;

    ServerExceptionStatus(Integer code, String message) {
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
