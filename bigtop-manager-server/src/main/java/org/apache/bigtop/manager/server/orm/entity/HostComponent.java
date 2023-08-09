package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(indexes = {@Index(name = "idx_host_id", columnList = "host_id"),
        @Index(name = "idx_component_id", columnList = "component_id")})
@TableGenerator(name = "host_component_generator", table = "sequence")
public class HostComponent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_component_generator")
    private Long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Host host;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Component component;
}
