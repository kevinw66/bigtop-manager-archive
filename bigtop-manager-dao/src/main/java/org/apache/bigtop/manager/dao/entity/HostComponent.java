package org.apache.bigtop.manager.dao.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.common.enums.MaintainState;
import org.apache.bigtop.manager.dao.converter.MaintainStateConverter;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "host_component", indexes = {@Index(name = "idx_hc_component_id", columnList = "component_id"),
        @Index(name = "idx_hc_host_id", columnList = "host_id")})
@TableGenerator(name = "host_component_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class HostComponent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_component_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "state")
    private MaintainState state;

    @ManyToOne
    @JoinColumn(name = "host_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Host host;

    @ManyToOne
    @JoinColumn(name = "component_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Component component;
}
