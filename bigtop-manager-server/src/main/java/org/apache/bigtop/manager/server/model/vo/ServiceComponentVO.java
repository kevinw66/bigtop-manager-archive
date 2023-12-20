package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ServiceComponentVO {

    private String serviceName;

    private List<ComponentVO> components;
}
