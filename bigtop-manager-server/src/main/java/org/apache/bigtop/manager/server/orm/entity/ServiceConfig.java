package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_type_version", columnNames = {"serviceName", "typeName", "version"})},
        indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id"),
                @Index(name = "idx_service_id", columnList = "service_id")})
@TableGenerator(name = "service_config_generator", table = "sequence")
public class ServiceConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "service_config_generator")
    private Long id;

    private String serviceName;

    private Integer version;

    private String versionGroup;

    private Boolean status;

    private String typeName;

    @Lob
    private String configData;

    private String configDesc;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Service service;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

}
