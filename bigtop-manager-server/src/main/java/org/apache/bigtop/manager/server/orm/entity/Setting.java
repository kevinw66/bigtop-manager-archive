package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "setting")
@TableGenerator(name = "settings_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Setting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "settings_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "type_name")
    private String typeName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "config_data", length = 16777216)
    private String configData;
}
