package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_repo_id", columnNames = {"repoId", "os", "arch"})})
@TableGenerator(name = "repo_generator", table = "sequence")
public class Repo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "repo_generator")
    private Long id;

    private String repoId;

    private String repoName;

    private String baseurl;

    private String os;

    private String arch;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_repo_stack_id"))
    private Stack stack;
}
