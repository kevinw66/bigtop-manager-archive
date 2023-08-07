package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;
import org.apache.bigtop.manager.common.pojo.stack.RepoInfo;

import java.util.List;

@Data
public class ClusterDTO {

    private String clusterName;

    private Integer clusterType;

    private String stackName;

    private String stackVersion;

    private List<RepoInfo> repoInfoList;

}
