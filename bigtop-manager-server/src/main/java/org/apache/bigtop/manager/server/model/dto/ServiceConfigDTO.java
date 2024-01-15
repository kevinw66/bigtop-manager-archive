package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ServiceConfigDTO implements Serializable {

    private String serviceName;

    private String configDesc;

    private Integer version;

    private List<TypeConfigDTO> configs;
}
