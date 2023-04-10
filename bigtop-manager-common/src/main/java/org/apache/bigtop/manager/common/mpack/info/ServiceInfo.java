package org.apache.bigtop.manager.common.mpack.info;

import lombok.Data;

import java.util.Collection;

@Data
public class ServiceInfo {

    private String name;

    private String displayName;

    private String version;

    private String comment;

    private Collection<ComponentInfo> components;

    private Collection<OsSpecific> osSpecifics;
}
