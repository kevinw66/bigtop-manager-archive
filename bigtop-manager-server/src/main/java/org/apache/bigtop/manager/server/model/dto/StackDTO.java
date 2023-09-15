package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class StackDTO {

    private String stackName;

    private String stackVersion;

    private String root;

    private String userGroup;

    private String packages;

    private String repoTemplate;

    private List<RepoDTO> repos;
}
