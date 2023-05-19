package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

@Data
public class HostVO {

    private Long id;

    private Long clusterId;

    private String hostname;

    private String ipv4;

    private String ipv6;

    private String osArch;

    private String osName;

    private Integer processorCount;

    private Long physicalMemory;
}
