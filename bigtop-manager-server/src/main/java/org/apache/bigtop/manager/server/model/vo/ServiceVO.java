package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ServiceVO {

    private Long id;

    private String serviceName;

    private String displayName;

    private String serviceDesc;

    private String serviceVersion;

    private String clusterName;

    private String serviceUser;

    private String serviceGroup;

    private List<String> requiredServices;

    private Boolean isClient;

    private Boolean isHealthy;
}
