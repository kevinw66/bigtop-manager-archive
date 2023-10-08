package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.model.dto.ScriptDTO;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id")})
@TableGenerator(name = "task_generator", table = "sequence")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "task_generator")
    private Long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Job job;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Stage stage;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

    private String messageId;

    private Long timeout;

    @Enumerated(EnumType.STRING)
    private JobState state;

    private String serviceName;

    private String componentName;

    @Enumerated(EnumType.STRING)
    private Command command;

    private String customCommand;

    private String customCommands;

    private String commandScript;

    private String hostname;

    private String root;

    private String stackName;

    private String stackVersion;

    private String serviceUser;

    private String serviceGroup;

    private String osSpecifics;
}
