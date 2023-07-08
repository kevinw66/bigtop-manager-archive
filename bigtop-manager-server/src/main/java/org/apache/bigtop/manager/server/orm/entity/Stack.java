package org.apache.bigtop.manager.server.orm.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@TableGenerator(name = "stack_generator", table = "sequence")
public class Stack {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "stack_generator")
    private Long id;

    private String stackName;

    private String stackVersion;

    private Timestamp createTime;

    private Timestamp updateTime;
}
