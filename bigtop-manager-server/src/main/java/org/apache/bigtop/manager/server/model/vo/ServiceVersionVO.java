package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

@Data
public class ServiceVersionVO {

    private String serviceName;

    private String displayName;

    private String serviceVersion;
}
