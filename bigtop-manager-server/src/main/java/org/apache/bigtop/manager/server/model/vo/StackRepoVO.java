package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

@Data
public class StackRepoVO {

    private String stackName;

    private String stackVersion;

    private String repoId;

    private String repoName;

    private String baseurl;

    private String os;

    private String arch;

}
