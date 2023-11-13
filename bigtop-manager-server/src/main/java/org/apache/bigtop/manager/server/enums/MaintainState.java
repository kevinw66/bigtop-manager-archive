package org.apache.bigtop.manager.server.enums;

import lombok.Getter;

@Getter
public enum MaintainState {
    UNINSTALLED,

    INSTALLED,

    MAINTAINED,

    STARTED,

    STOPPED,

}
