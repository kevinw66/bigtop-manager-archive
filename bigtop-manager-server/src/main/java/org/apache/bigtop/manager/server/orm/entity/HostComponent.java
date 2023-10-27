package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.server.enums.heartbeat.CommandState;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "host_component")
@TableGenerator(name = "host_component_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class HostComponent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_component_generator")
    @Column(name = "id")
    private Long id;

    // 0: not installed, 1: installed, 2: maintained
    @Column(name = "status")
    private Integer status;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private CommandState state;

    @ManyToOne
    @JoinColumn(name = "host_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Host host;

    @ManyToOne
    @JoinColumn(name = "component_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Component component;
}
