package org.apache.bigtop.manager.server.orm.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@TableGenerator(name = "component_generator", table = "sequence")
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "component_generator")
    private Long id;

    private String componentName;

    private String scriptId;

    private Timestamp createTime;

    private Timestamp updateTime;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;


}
