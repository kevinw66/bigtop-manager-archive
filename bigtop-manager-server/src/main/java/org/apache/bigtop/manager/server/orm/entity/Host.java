package org.apache.bigtop.manager.server.orm.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@TableGenerator(name = "host_generator", table = "sequence")
public class Host {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_generator")
    private Long id;

    private Long clusterId;

    private String hostname;

    private String ipv4;

    private String ipv6;

    private String osArch;

    private String osName;

    private Integer processorCount;

    private Long physicalMemory;

    private Timestamp createTime;

    private Timestamp updateTime;
}
