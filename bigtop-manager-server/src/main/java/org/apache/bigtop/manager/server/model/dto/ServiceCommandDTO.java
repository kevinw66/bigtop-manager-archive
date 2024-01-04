package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ServiceCommandDTO implements Serializable {

    private String serviceName;

    private List<ComponentHostDTO> componentHosts;

    private List<ConfigDataDTO> configs;
}
