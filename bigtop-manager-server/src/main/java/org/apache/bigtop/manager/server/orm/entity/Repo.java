package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id")})
@TableGenerator(name = "repo_generator", table = "sequence")
public class Repo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "repo_generator")
    private Long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

    private String baseUrl;

    private String os;

    private String arch;

    private String repoId;

    private String repoName;
}
