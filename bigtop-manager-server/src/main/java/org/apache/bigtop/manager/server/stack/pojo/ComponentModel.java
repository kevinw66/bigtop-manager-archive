package org.apache.bigtop.manager.server.stack.pojo;

import lombok.Data;

import java.util.Map;

@Data
public class ComponentModel {

    private String name;

    private String displayName;

    private String category;

    private String cardinality;

    private ScriptModel commandScript;

    private Map<String, ScriptModel> customCommands;
}
