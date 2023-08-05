package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_username", columnNames = {"username"})})
@TableGenerator(name = "user_generator", table = "sequence", allocationSize = 1)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_generator")
    private Long id;

    private String username;

    private String password;

    private Boolean status;

}
