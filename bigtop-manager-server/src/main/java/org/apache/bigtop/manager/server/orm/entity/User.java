package org.apache.bigtop.manager.server.orm.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@TableGenerator(name = "user_generator", table = "sequence")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_generator")
    private Long id;

    private String username;

    private String password;

    private Boolean status;

    private Timestamp createTime;

    private Timestamp updateTime;
}
