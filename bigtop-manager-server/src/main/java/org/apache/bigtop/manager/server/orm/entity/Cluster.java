package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_cluster_name", columnNames = {"clusterName"})},
        indexes = {@Index(name = "idx_stack_id", columnList = "stack_id")})
@TableGenerator(name = "cluster_generator", table = "sequence")
public class Cluster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "cluster_generator")
    private Long id;

    private String clusterName;

    private String clusterDesc;

    private Integer clusterType;

    private String cacheDir;

    private String root;

    private String userGroup;

    private String packages;

    private String repoTemplate;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Stack stack;
}
