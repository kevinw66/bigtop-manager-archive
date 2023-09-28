package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

@Data
public class StackRepoVO {

    private String repoId;

    private String baseurl;

    private String os;

    private String arch;

}
