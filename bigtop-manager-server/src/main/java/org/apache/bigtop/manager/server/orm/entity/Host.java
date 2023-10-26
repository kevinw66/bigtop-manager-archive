package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "host")
@TableGenerator(name = "host_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Host extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "hostname")
    private String hostname;

    @Column(name = "ipv4")
    private String ipv4;

    @Column(name = "ipv6")
    private String ipv6;

    @Column(name = "arch")
    private String arch;

    @Column(name = "os")
    private String os;

    @Column(name = "available_processors")
    private Integer availableProcessors;

    @Column(name = "total_memory_size")
    private Long totalMemorySize;

    @Column(name = "state")
    private String state;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

}
