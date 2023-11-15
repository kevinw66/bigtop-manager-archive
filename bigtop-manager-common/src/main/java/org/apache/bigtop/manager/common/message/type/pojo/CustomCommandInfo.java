package org.apache.bigtop.manager.common.message.type.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomCommandInfo {

    private String name;

    private ScriptInfo commandScript;
}
