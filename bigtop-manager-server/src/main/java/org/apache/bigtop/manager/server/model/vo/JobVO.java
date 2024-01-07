package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;
import org.apache.bigtop.manager.server.enums.JobState;

import java.util.List;

@Data
public class JobVO {

    private Long id;

    private List<StageVO> stages;

    private JobState state;

    private String name;
}
