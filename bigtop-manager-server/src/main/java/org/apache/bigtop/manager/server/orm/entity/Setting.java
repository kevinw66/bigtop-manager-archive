package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_version", columnNames = {"version"})})
@TableGenerator(name = "settings_generator", table = "sequence")
public class Setting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "settings_generator")
    private Long id;

    private Integer version;

    private String javaHome;

    private String javaVersion;

    private String jdbcDriver;

    private String jdbcDriverHome;

    private String serverHostname;

    private String serverPort;

}
