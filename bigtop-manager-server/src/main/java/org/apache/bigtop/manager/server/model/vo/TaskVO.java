package org.apache.bigtop.manager.server.model.vo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.orm.entity.BaseEntity;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Stage;

@Data
public class TaskVO {

    private Long id;

    private JobState state;

    private String hostname;
}
