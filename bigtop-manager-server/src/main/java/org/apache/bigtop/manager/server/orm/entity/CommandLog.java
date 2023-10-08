package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(indexes = {@Index(name = "idx_task_id", columnList = "task_id")})
@TableGenerator(name = "command_log_generator", table = "sequence")
public class CommandLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "command_log_generator")
    private Long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Job job;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Stage stage;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Task task;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 16777216)
    private String result;

    private String hostname;

}
