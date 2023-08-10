package org.apache.bigtop.manager.common.pojo.stack;

import lombok.Data;

@Data
public class ComponentInfo {

    private String componentName;

    private String componentDisplay;

    private String category;

    private String cardinality;

    private ScriptInfo commandScript;
}
