package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "repo")
@TableGenerator(name = "repo_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Repo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "repo_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "os")
    private String os;

    @Column(name = "arch")
    private String arch;

    @Column(name = "repo_id")
    private String repoId;

    @Column(name = "repo_name")
    private String repoName;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;
}
