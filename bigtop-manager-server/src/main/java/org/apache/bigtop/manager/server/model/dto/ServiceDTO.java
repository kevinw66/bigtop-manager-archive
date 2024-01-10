package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ServiceDTO {

    private String serviceName;

    private String displayName;

    private String serviceDesc;

    private String serviceVersion;

    private String serviceUser;

    private String serviceGroup;

    private List<OSSpecificDTO> osSpecifics;

    private List<ComponentDTO> components;

    private List<String> requiredServices;
}
