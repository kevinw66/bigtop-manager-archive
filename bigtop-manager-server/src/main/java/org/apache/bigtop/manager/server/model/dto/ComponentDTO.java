package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ComponentDTO {

    private String componentName;

    private String displayName;

    private String category;

    private String cardinality;

    private ScriptDTO commandScript;

    private Map<String, ScriptDTO> customCommands;
}
