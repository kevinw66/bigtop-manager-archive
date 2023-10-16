package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_hostname", columnNames = {"hostname"})},
        indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id")})
@TableGenerator(name = "host_generator", table = "sequence")
public class Host extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_generator")
    private Long id;

    private String hostname;

    private String ipv4;

    private String ipv6;

    private String arch;

    private String os;

    private Integer availableProcessors;

    private Long totalMemorySize;

    private String state;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

}
