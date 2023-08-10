package org.apache.bigtop.manager.server.stack.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ServiceModel {

    private String name;

    private String displayName;

    private String desc;

    private String version;

    private String user;

    private String group;

    private List<OSSpecificModel> osSpecifics;

    private List<ComponentModel> components;
}
