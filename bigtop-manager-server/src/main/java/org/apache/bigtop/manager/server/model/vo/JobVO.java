package org.apache.bigtop.manager.server.model.vo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.orm.entity.BaseEntity;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Stage;

import java.util.List;

@Data
public class JobVO {

    private Long id;

    private List<StageVO> stages;

    private JobState state;

    private String context;
}
