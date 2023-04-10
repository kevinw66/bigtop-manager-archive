package org.apache.bigtop.manager.mpack.info;

import lombok.Data;

@Data
public class RepositoryInfo {

    private String baseUrl;

    private String os;

    private String repoId;

    private String repoName;

    private String mirrorsList;
}
