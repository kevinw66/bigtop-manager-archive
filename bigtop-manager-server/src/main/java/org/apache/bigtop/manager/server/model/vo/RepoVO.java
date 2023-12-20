package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

@Data
public class RepoVO {

    private String repoId;

    private String repoName;

    private String baseUrl;

    private String os;

    private String arch;

}
