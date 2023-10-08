package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_component_name", columnNames = {"componentName", "cluster_id"})},
        indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id"),
                @Index(name = "idx_service_id", columnList = "service_id")})
@TableGenerator(name = "component_generator", table = "sequence")
public class Component extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "component_generator")
    private Long id;

    private String componentName;

    private String displayName;

    private String commandScript;

    private String customCommands;

    private String category;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Service service;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;


}
