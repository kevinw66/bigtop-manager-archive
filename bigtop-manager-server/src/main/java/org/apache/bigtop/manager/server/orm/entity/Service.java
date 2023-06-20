package org.apache.bigtop.manager.server.orm.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@TableGenerator(name = "service_generator", table = "sequence")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "service_generator")
    private Long id;

    private String serviceName;

    private String OSSpecifics;

    private Timestamp createTime;

    private Timestamp updateTime;

    @ManyToOne
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;


}
