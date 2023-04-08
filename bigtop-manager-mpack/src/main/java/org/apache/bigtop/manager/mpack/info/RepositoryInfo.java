package org.apache.bigtop.manager.mpack.info;

import lombok.Data;

@Data
public class RepositoryInfo {
    private String baseUrl;
    private String osType;
    private String repoId;
    private String repoName;
    private String distribution;
    private String components;
    private String mirrorsList;
    private String defaultBaseUrl;
    private boolean repoSaved = false;
    private boolean unique = false;
}
