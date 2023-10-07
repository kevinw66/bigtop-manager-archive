package org.apache.bigtop.manager.server.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepoReq {

    private String repoId;

    private String repoName;

    private String baseUrl;

    private String os;

    private String arch;

}
