package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.bigtop.manager.server.enums.JobState;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "stage")
@TableGenerator(name = "stage_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Stage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "stage_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private JobState state;

    @Column(name = "stage_order")
    private Integer stageOrder;

    @ManyToOne
    @JoinColumn(name = "job_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Job job;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

    @ToString.Exclude
    @OneToMany(mappedBy = "stage")
    private List<Task> tasks;
}
