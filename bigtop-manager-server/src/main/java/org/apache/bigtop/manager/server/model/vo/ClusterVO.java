package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

@Data
public class ClusterVO {

    private Long id;

    private String clusterName;

    private Integer clusterType;
}
