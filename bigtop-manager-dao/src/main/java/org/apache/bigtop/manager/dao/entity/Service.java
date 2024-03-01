package org.apache.bigtop.manager.dao.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service", uniqueConstraints = {@UniqueConstraint(name = "uk_service_name", columnNames = {"service_name", "cluster_id"})},
        indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id")})
@TableGenerator(name = "service_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Service extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "service_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "service_desc")
    private String serviceDesc;

    @Column(name = "service_version")
    private String serviceVersion;

    @Column(name = "os_specifics")
    private String osSpecifics;

    @Column(name = "service_user")
    private String serviceUser;

    @Column(name = "service_group")
    private String serviceGroup;

    @Column(name = "required_service")
    private String requiredServices;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;
}
