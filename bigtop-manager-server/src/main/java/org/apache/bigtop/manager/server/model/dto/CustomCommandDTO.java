package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

@Data
public class CustomCommandDTO {

    private String name;

    private ScriptDTO commandScript;
}
