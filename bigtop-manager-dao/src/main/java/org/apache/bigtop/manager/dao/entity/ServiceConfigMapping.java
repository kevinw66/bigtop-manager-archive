package org.apache.bigtop.manager.dao.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_config_mapping", indexes = {@Index(name = "idx_scm_sc_id", columnList = "service_config_id"),
        @Index(name = "idx_scm_scr_id", columnList = "service_config_record_id")})
@TableGenerator(name = "service_config_mapping_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class ServiceConfigMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "service_config_mapping_generator")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_config_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ServiceConfig serviceConfig;

    @ManyToOne
    @JoinColumn(name = "service_config_record_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ServiceConfigRecord serviceConfigRecord;

}
