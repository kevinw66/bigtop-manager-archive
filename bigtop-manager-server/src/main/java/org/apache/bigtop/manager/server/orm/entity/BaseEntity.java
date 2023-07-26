package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.sql.Timestamp;

@Data
@MappedSuperclass
public abstract class BaseEntity {

    private Timestamp createTime;

    private Timestamp updateTime;

}
