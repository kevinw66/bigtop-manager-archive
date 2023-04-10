package org.apache.bigtop.manager.common.mpack.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StackInfo {

    private boolean active;

    private String name;

    private String version;

    private String extend;

    private String root;

    private String repoTemplate;

    private List<RepositoryInfo> repos;

    private Collection<ServiceInfo> services;

}
