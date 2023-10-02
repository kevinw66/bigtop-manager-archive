package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_type_name", columnNames = {"typeName"})})
@TableGenerator(name = "settings_generator", table = "sequence")
public class Setting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "settings_generator")
    private Long id;

    private String typeName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 16777216)
    private String configData;

}
