package org.apache.bigtop.manager.common.pojo.stack;

import lombok.Data;

@Data
public class ComponentInfo {

    private String name;

    private String displayName;

    private String category;

    private String cardinality;

    private ScriptInfo commandScript;
}
