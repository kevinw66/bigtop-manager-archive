package org.apache.bigtop.manager.common.pojo.stack;

import lombok.Data;

import java.util.List;

@Data
public class ServiceInfo {

    private String name;

    private String displayName;

    private String desc;

    private String version;

    private String user;

    private String group;

    private List<OSSpecific> osSpecifics;

    private List<ComponentInfo> components;
}
