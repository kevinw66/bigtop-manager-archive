package org.apache.bigtop.manager.server.job.stage;

import lombok.Data;
import org.apache.bigtop.manager.server.model.dto.RepoDTO;

import java.util.List;

@Data
public class StageLifecycleContext {

    private Long clusterId;

    private String clusterName;

    private String stackName;

    private String stackVersion;

    private List<String> hostnames;

    private String serviceName;

    private String componentName;

    private List<RepoDTO> repoInfoList;
}
