package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_component_name", columnNames = {"componentName"})})
@TableGenerator(name = "component_generator", table = "sequence")
public class Component extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "component_generator")
    private Long id;

    private String componentName;

    private String scriptId;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_component_service_id"))
    private Service service;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_component_cluster_id"))
    private Cluster cluster;


}
