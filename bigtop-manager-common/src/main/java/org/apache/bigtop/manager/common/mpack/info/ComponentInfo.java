package org.apache.bigtop.manager.common.mpack.info;

import lombok.Data;

@Data
public class ComponentInfo {
    String name;
    String displayName;
    String category;
    String cardinality;
    CommandScript commandScript;
}

class CommandScript {
    String script;
    String scriptType;
    String timeout;
}
