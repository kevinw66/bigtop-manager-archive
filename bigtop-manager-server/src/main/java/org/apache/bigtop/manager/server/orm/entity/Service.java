package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_service_name", columnNames = {"serviceName"})})
@TableGenerator(name = "service_generator", table = "sequence")
public class Service extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "service_generator")
    private Long id;

    private String serviceName;

    private String serviceDesc;

    private String serviceVersion;

    private String osSpecifics;

    private String serviceUser;

    private String serviceGroup;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_service_cluster_id"))
    private Cluster cluster;


}
