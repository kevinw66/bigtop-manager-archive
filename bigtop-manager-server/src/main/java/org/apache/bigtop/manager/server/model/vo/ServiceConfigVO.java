package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ServiceConfigVO {

    private String serviceName;

    private String configDesc;

    private List<ConfigDataVO> configs;
}
