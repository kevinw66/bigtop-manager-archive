package org.apache.bigtop.manager.common.message.type.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HostInfo implements Serializable {

    private String hostname;

    private String ipv4;

    private String ipv6;

    private String name;

    private String version;

    private String arch;

    private BigDecimal systemCpuLoad;

    private Integer availableProcessors;

    private BigDecimal processCpuLoad;

    private Long processCpuTime;

    private Long totalPhysicalMemorySize;

    private Long freePhysicalMemorySize;

    private Long totalSwapSpaceSize;

    private Long freeSwapSpaceSize;

    private Long committedVirtualMemorySize;

    private BigDecimal systemLoadAverage;
}
