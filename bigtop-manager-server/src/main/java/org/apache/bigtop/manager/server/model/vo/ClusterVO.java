package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;
import org.apache.bigtop.manager.server.enums.MaintainState;

@Data
public class ClusterVO {

    private Long id;

    private String clusterName;

    private Integer clusterType;

    private String stackName;

    private String stackVersion;

    private Boolean selected;

    private MaintainState state;
}
