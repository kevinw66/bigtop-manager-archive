package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_stack_name", columnNames = {"stackName", "stackVersion"})})
@TableGenerator(name = "stack_generator", table = "sequence")
public class Stack extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "stack_generator")
    private Long id;

    private String stackName;

    private String stackVersion;

}
