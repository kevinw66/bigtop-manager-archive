package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

@Data
public class ServiceVO {

    private Long id;

    private String serviceName;

    private String displayName;

    private String serviceDesc;

    private String serviceVersion;

    private String clusterName;

    private Integer status;
}
