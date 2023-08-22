package org.apache.bigtop.manager.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepoDTO {

    private String repoId;

    private String repoName;

    private String baseurl;

    private String os;

    private String arch;

}
