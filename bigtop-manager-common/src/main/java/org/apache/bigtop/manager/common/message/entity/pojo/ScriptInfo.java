package org.apache.bigtop.manager.common.message.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptInfo {

    private String scriptType;

    private String scriptId;

    private Long timeout;
}
