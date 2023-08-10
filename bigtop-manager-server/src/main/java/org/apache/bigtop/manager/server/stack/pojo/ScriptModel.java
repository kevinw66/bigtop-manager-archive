package org.apache.bigtop.manager.server.stack.pojo;

import lombok.Data;

@Data
public class ScriptModel {

    private String scriptType;

    private String scriptId;

    private Long timeout;
}
