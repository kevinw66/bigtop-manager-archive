package org.apache.bigtop.manager.common.pojo.stack;

import lombok.Data;

import java.util.List;

@Data
public class ServiceInfo {

    private String serviceName;

    private String serviceDisplay;

    private String serviceDesc;

    private String serviceVersion;

    private String serviceUser;

    private String serviceGroup;

    private List<OSSpecific> osSpecifics;

    private List<ComponentInfo> components;
}
