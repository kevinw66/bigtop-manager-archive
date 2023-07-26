package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "repo_os")
@TableGenerator(name = "repo_os_generator", table = "sequence")
public class RepoOS extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "repo_os_generator")
    private Long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ro_os_id"))
    private OS os;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ro_repo_id"))
    private Repo repo;
}
