package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;
import org.apache.bigtop.manager.common.enums.JobState;
import java.util.List;

@Data
public class StageVO {

    private Long id;

    private String name;

    private List<TaskVO> tasks;

    private JobState state;

    private Integer stageOrder;

    private String createTime;

    private String updateTime;
}
