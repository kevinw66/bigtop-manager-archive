package org.apache.bigtop.manager.dao.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "type_config")
@TableGenerator(name = "type_config_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class TypeConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "type_config_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "type_name")
    private String typeName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "properties_json", length = 16777216)
    private String propertiesJson;

    @ManyToOne
    @JoinColumn(name = "service_config_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ServiceConfig serviceConfig;
}
