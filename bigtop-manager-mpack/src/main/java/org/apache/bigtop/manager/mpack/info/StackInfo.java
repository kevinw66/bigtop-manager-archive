package org.apache.bigtop.manager.mpack.info;

import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class StackInfo {
    private String minJdk;
    private String maxJdk;
    private String name;
    private String version;

    private boolean active;

    private List<RepositoryInfo> repositories;
    private Collection<ServiceInfo> services;

    private boolean valid = true;

}
