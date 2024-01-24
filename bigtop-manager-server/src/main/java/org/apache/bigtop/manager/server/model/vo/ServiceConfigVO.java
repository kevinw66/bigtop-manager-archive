package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ServiceConfigVO {

    private String serviceName;

    private String configDesc;

    private Integer version;

    private List<TypeConfigVO> configs;

    private String createTime;

    private String updateTime;

}
