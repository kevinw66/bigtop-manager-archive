package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClusterDTO {

    private String clusterName;

    private Integer clusterType;

    private String stackName;

    private String stackVersion;

    private List<RepoDTO> repoInfoList;

}
