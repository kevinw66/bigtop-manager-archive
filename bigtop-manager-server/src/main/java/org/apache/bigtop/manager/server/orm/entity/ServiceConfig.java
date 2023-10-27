package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_config")
@TableGenerator(name = "service_config_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class ServiceConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "service_config_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "version")
    private Integer version;

    @Column(name = "type_name")
    private String typeName;

    @Lob
    @Basic(fetch= FetchType.LAZY)
    @Column(name = "config_data", length = 16777216)
    private String configData;

    @Lob
    @Basic(fetch= FetchType.LAZY)
    @Column(name = "config_attributes",length = 16777216)
    private String configAttributes;

    @ManyToOne
    @JoinColumn(name = "service_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Service service;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

}
