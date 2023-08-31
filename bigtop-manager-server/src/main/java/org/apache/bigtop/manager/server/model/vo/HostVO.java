package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

@Data
public class HostVO {

    private Long id;

    private String clusterName;

    private String hostname;

    private String ipv4;

    private String ipv6;

    private String arch;

    private String os;

    private Integer availableProcessors;

    private Long totalMemorySize;

    private Integer status;
}
