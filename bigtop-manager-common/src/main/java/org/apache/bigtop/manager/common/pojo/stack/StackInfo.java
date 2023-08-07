package org.apache.bigtop.manager.common.pojo.stack;

import lombok.Data;

import java.util.List;

@Data
public class StackInfo {

    private String stackName;

    private String stackVersion;

    private String root;

    private String userGroup;

    private String cacheDir;

    private String packages;

    private String repoTemplate;

    private List<RepoInfo> repos;
}
