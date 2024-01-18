package org.apache.bigtop.manager.server.model.dto.command;

import lombok.Data;
import org.apache.bigtop.manager.server.model.dto.RepoDTO;

import java.util.List;

@Data
public class ClusterCommandDTO {

    private String clusterName;

    private Integer clusterType;

    private String stackName;

    private String stackVersion;

    private List<RepoDTO> repoInfoList;

    private List<String> hostnames;

}
