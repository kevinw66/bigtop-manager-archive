package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "command_log", indexes = {@Index(name = "idx_job_id", columnList = "job_id"),
        @Index(name = "idx_stage_id", columnList = "stage_id"),
        @Index(name = "idx_task_id", columnList = "task_id")})
@TableGenerator(name = "command_log_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class CommandLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "command_log_generator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "result", length = 16777216)
    private String result;

    @Column(name = "hostname")
    private String hostname;

    @ManyToOne
    @JoinColumn(name = "job_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Job job;

    @ManyToOne
    @JoinColumn(name = "stage_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Stage stage;

    @ManyToOne
    @JoinColumn(name = "task_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Task task;

}
