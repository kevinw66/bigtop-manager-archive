package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_config_record")
@TableGenerator(name = "service_config_record_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class ServiceConfigRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "service_config_record_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "version")
    private Integer version;

    @Column(name = "version_group")
    private String versionGroup;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "config_desc", length = 16777216)
    private String configDesc;

    @ManyToOne
    @JoinColumn(name = "service_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Service service;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

}
