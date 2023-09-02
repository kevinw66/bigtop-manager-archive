package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConfigurationDTO {

    private String clusterName;

    private String serviceName;

    private String configDesc;

    private List<ConfigDataDTO> configurations;
}
