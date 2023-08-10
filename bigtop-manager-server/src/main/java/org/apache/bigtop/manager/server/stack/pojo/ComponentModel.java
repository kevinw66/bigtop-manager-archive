package org.apache.bigtop.manager.server.stack.pojo;

import lombok.Data;

@Data
public class ComponentModel {

    private String name;

    private String displayName;

    private String category;

    private String cardinality;

    private ScriptModel commandScript;
}
