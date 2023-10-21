package org.apache.bigtop.manager.common.message.type.pojo;

import lombok.Data;

@Data
public class ComponentInfo {

    private String componentName;

    private String displayName;

    private String commandScript;

    private String customCommands;

    private String category;

    private String serviceName;

}
