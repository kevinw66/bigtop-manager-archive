package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

@Data
public class ServiceDTO {

    private String clusterName;

    private Integer clusterType;

    private String stackName;

    private String stackVersion;

    private String serviceName;

    private String displayName;

    private String serviceDesc;
}
