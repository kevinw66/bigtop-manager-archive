package org.apache.bigtop.manager.server.enums;

import lombok.Getter;

@Getter
public enum StatusType {
    UNINSTALLED(0, "uninstalled"),
    INSTALLED(1, "installed"),
    MAINTAINED(2, "maintained");

    private final int code;
    private final String name;

    StatusType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
