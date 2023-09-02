package org.apache.bigtop.manager.server.enums;

import lombok.Getter;

@Getter
public enum ServerExceptionStatus {

    NEED_LOGIN(10000, "Not logged in"),
    USERNAME_OR_PASSWORD_REQUIRED(10001, "Username or password should not be empty"),
    INCORRECT_USERNAME_OR_PASSWORD(10002, "Incorrect username or password"),

    // Cluster Exceptions -- 11000 ~ 11999
    CLUSTER_NOT_FOUND(11000, "Cluster not exist"),

    // Host Exceptions -- 12000 ~ 12999
    HOST_NOT_FOUND(12000, "Host not exist"),

    // Host Exceptions -- 13000 ~ 13999
    STACK_NOT_FOUND(13000, "Stack not exist"),
    STACK_CHECK_INVALID(13001, "Stack check invalid"),

    // Service Exceptions -- 13000 ~ 13999
    SERVICE_NOT_FOUND(14000, "Service not exist"),

    // Component Exceptions -- 13000 ~ 13999
    COMPONENT_NOT_FOUND(15000, "Component not exist"),
    ;

    private final Integer code;

    private final String message;

    ServerExceptionStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
