package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_os_name", columnNames = {"osName"})})
@TableGenerator(name = "os_generator", table = "sequence")
public class OS extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "os_generator")
    private Long id;

    private String osName;

    private String osVersion;

    private String osArch;

}
