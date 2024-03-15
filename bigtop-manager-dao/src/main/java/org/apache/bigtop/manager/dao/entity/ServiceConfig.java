package org.apache.bigtop.manager.dao.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_config",
        indexes = {@Index(name = "idx_sc_cluster_id", columnList = "cluster_id"),
                @Index(name = "idx_sc_service_id", columnList = "service_id")})
@TableGenerator(name = "service_config_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class ServiceConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "service_config_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "config_desc")
    private String configDesc;

    @Column(name = "version")
    private Integer version;

    @Column(name = "selected")
    private Boolean selected;

    @ToString.Exclude
    @OneToMany(mappedBy = "serviceConfig")
    private List<TypeConfig> configs;

    @ManyToOne
    @JoinColumn(name = "service_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Service service;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

}
