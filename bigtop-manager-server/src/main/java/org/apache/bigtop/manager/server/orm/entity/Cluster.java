package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cluster")
@TableGenerator(name = "cluster_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Cluster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "cluster_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "cluster_name")
    private String clusterName;

    @Column(name = "cluster_desc")
    private String clusterDesc;

    @Column(name = "cluster_type")
    private Integer clusterType;

    @Column(name = "root")
    private String root;

    @Column(name = "user_group")
    private String userGroup;

    @Column(name = "packages")
    private String packages;

    @Column(name = "repo_template")
    private String repoTemplate;

    // 0: not installed, 1: installed, 2: maintained
    @Column(name = "status")
    private Integer status;

    @Column(name = "selected")
    private Boolean selected;

    @ManyToOne
    @JoinColumn(name = "stack_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Stack stack;
}
