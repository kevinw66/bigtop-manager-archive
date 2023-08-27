package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id")})
@TableGenerator(name = "request_generator", table = "sequence")
public class Request extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "request_generator")
    private Long id;

    private String command;

    private String state;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

}
