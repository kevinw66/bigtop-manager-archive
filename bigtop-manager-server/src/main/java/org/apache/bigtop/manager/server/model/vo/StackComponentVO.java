package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

@Data
public class StackComponentVO {

    private String componentName;

    private String displayName;

    private String category;

    private String cardinality;

    private String serviceName;
}
