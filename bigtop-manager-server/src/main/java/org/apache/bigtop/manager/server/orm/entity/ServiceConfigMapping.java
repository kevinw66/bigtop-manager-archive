package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_config_record", columnNames = {"service_config_id", "service_config_record_id"})},
        indexes = {@Index(name = "idx_service_config_id", columnList = "service_config_id"),
                @Index(name = "idx_service_config_record_id", columnList = "service_config_record_id")})
@TableGenerator(name = "service_config_mapping_generator", table = "sequence")
public class ServiceConfigMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "service_config_mapping_generator")
    private Long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ServiceConfig serviceConfig;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ServiceConfigRecord serviceConfigRecord;

}
