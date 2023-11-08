package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.bigtop.manager.server.enums.JobState;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "job")
@TableGenerator(name = "job_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "job_generator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private JobState state;

    @Lob
    @Basic(fetch= FetchType.LAZY)
    @Column(name = "context", length = 16777216)
    private String context;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

    @ToString.Exclude
    @OneToMany(mappedBy = "job")
    private List<Stage> stages;
}
