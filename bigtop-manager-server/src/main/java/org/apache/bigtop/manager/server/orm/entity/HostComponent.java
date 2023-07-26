package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TableGenerator(name = "host_component_generator", table = "sequence")
public class HostComponent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_component_generator")
    private Long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_hc_host_id"))
    private Host host;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_hc_component_id"))
    private Component component;
}
