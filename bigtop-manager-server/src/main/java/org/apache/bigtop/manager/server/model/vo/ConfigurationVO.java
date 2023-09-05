package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ConfigurationVO {

    private String clusterName;

    private String serviceName;

    private String configDesc;

    private Integer version;

    private List<ConfigDataVO> configurations;

}
