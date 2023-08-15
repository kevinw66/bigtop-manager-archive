package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

@Data
public class ScriptDTO {

    private String scriptType;

    private String scriptId;

    private Long timeout;
}
