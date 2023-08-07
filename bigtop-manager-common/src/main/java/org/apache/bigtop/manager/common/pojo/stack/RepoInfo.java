package org.apache.bigtop.manager.common.pojo.stack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepoInfo {

    private String repoId;

    private String repoName;

    private String baseurl;

    private String os;

    private String arch;

}
