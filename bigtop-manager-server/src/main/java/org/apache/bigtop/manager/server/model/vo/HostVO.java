package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;
import org.apache.bigtop.manager.common.enums.MaintainState;

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

    private Long freeMemorySize;

    private Long totalMemorySize;

    private Long freeDisk;

    private Long totalDisk;

    private MaintainState state;
}
