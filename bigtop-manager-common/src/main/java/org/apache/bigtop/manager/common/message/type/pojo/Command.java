package org.apache.bigtop.manager.common.message.type.pojo;

public class Command {

    // INSTALL、CONFIGURE、START and etc
    private String commandType;

    // shell、python and etc
    private String scriptType;

    // params to pass when run script
    private String[] params;

    // command run timeout
    private Integer timeout;
}
