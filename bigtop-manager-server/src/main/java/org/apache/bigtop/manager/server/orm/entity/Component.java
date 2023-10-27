package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "component")
@TableGenerator(name = "component_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Component extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "component_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "component_name")
    private String componentName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "command_script")
    private String commandScript;

    @Column(name = "custom_commands")
    private String customCommands;

    @Column(name = "category")
    private String category;

    @ManyToOne
    @JoinColumn(name = "service_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Service service;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;


}
