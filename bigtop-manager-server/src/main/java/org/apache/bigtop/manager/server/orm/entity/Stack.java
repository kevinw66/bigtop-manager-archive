package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "stack", uniqueConstraints = {@UniqueConstraint(name = "uk_stack", columnNames = {"stack_name", "stack_version"})})
@TableGenerator(name = "stack_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class Stack extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "stack_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "stack_name")
    private String stackName;

    @Column(name = "stack_version")
    private String stackVersion;

}
