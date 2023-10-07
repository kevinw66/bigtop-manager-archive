package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_repo_id", columnNames = {"repoId", "os", "arch"})},
        indexes = {@Index(name = "idx_stack_id", columnList = "stack_id")})
@TableGenerator(name = "repo_generator", table = "sequence")
public class Repo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "repo_generator")
    private Long id;

    private String repoId;

    private String repoName;

    private String baseUrl;

    private String os;

    private String arch;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Stack stack;
}
