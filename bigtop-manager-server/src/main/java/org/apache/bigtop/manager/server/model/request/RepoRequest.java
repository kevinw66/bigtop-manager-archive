package org.apache.bigtop.manager.server.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepoRequest {

    private String repoId;

    private String repoName;

    private String baseurl;

    private String os;

    private String arch;

}
