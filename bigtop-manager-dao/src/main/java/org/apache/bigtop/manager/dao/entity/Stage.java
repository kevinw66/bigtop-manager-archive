package org.apache.bigtop.manager.dao.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.bigtop.manager.common.enums.JobState;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "stage", indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id"),
        @Index(name = "idx_job_id", columnList = "job_id")})
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

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "component_name")
    private String componentName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "payload", length = 16777216)
    private String payload;

    @ManyToOne
    @JoinColumn(name = "job_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Job job;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

    @ToString.Exclude
    @OneToMany(mappedBy = "stage")
    private List<Task> tasks;

    @Column(name = "callback_class_name")
    private String callbackClassName;

}
