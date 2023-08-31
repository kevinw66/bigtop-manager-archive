package org.apache.bigtop.manager.server.enums;

public enum StatusType {
    UNINSTALL(0, "uninstalled"),
    INSTALLED(1, "installed"),
    MAINTAINED(2, "maintained");

    private final int code;
    private final String name;

    StatusType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
}
