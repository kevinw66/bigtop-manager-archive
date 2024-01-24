package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;
import org.apache.bigtop.manager.server.enums.JobState;

@Data
public class TaskVO {

    private Long id;

    private String name;

    private JobState state;

    private String hostname;

    private String createTime;

    private String updateTime;

}
