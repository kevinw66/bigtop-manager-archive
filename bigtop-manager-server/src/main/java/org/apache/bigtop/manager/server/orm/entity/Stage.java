package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.bigtop.manager.server.enums.JobState;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id")})
@TableGenerator(name = "stage_generator", table = "sequence")
public class Stage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "stage_generator")
    private Long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Job job;

    @Transient
    @ToString.Exclude
    @OneToMany(mappedBy = "stage")
    private List<Task> tasks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private JobState state;

    private String desc;

    private Long dependsOn;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;
}
