package org.apache.bigtop.manager.server.orm.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@TableGenerator(name = "host_component_generator", table = "sequence")
public class HostComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "host_component_generator")
    private Long id;

    private Timestamp createTime;

    private Timestamp updateTime;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;

    @ManyToOne
    @JoinColumn(name = "component_id")
    private Component component;
}
