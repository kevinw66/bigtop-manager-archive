package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;
import org.apache.bigtop.manager.common.enums.MaintainState;

@Data
public class HostComponentVO {

    private Long id;

    private String componentName;

    private String displayName;

    private String category;

    private String serviceName;

    private String clusterName;

    private String hostname;

    private MaintainState state;
}
