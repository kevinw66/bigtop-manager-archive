package org.apache.bigtop.manager.dao.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.common.enums.MaintainState;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cluster", uniqueConstraints = {@UniqueConstraint(name = "uk_cluster_name", columnNames = {"cluster_name"})},
        indexes = {@Index(name = "idx_stack_id", columnList = "stack_id")})
@TableGenerator(name = "cluster_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Cluster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "cluster_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "cluster_name")
    private String clusterName;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private MaintainState state;

    @Column(name = "selected")
    private Boolean selected;

    @ManyToOne
    @JoinColumn(name = "stack_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Stack stack;
}
