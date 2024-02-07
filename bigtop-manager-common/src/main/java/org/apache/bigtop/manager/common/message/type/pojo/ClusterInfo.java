package org.apache.bigtop.manager.common.message.type.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ClusterInfo {

    private String clusterName;

    private String stackName;

    private String stackVersion;

    private String userGroup;

    private String root;

    private String repoTemplate;

    private List<String> packages;
}
