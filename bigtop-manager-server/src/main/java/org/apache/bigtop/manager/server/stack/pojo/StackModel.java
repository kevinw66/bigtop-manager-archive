package org.apache.bigtop.manager.server.stack.pojo;

import lombok.Data;

import java.util.List;

@Data
public class StackModel {

    private String stackName;

    private String stackVersion;

    private String root;

    private String userGroup;

    private String packages;

    private String repoTemplate;

    private List<RepoModel> repos;
}
