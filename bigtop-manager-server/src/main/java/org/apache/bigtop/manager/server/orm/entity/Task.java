package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.JobState;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "task")
@TableGenerator(name = "task_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "task_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "message_id")
    private String messageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private JobState state;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "component_name")
    private String componentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "command")
    private Command command;

    @Column(name = "custom_command")
    private String customCommand;

    @Column(name = "custom_commands")
    private String customCommands;

    @Column(name = "command_script")
    private String commandScript;

    @Column(name = "hostname")
    private String hostname;

    @Column(name = "stack_name")
    private String stackName;

    @Column(name = "stack_version")
    private String stackVersion;

    @Column(name = "service_user")
    private String serviceUser;

    @Column(name = "service_group")
    private String serviceGroup;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "content", length = 16777216)
    private String content;

    @ManyToOne
    @JoinColumn(name = "job_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Job job;

    @ManyToOne
    @JoinColumn(name = "stage_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Stage stage;

    @ManyToOne
    @JoinColumn(name = "cluster_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;
}
