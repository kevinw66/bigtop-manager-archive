package org.apache.bigtop.manager.stack.common.enums;

import lombok.Getter;

@Getter
public enum HookAroundType {
    BEFORE("before"), AFTER("after");

    HookAroundType(String type) {
        this.type = type;
    }

    private final String type;

}
