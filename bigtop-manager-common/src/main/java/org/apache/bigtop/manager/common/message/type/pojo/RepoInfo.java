package org.apache.bigtop.manager.common.message.type.pojo;

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
