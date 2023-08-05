package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_hostname", columnNames = {"hostname"})})
@TableGenerator(name = "host_generator", table = "sequence", allocationSize = 1)
public class Host extends BaseEntity {

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

}
