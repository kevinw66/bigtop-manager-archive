package org.apache.bigtop.manager.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.bigtop.manager.common.enums.JobState;
import jakarta.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "job", indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id")})
@TableGenerator(name = "job_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "job_generator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private JobState state;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

    @ToString.Exclude
    @OneToMany(mappedBy = "job")
    private List<Stage> stages;
}
