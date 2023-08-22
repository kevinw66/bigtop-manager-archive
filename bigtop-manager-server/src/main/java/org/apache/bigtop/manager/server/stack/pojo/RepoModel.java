package org.apache.bigtop.manager.server.stack.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepoModel {

    private String repoId;

    private String repoName;

    private String baseurl;

    private String os;

    private String arch;

}
