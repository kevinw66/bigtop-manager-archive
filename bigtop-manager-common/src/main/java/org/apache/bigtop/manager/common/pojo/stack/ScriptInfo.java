package org.apache.bigtop.manager.common.pojo.stack;

import lombok.Data;

@Data
public class ScriptInfo {

    private String scriptType;

    private String scriptId;

    private Long timeout;
}
