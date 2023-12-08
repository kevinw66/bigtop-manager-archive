package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ConfigDataVO {

    private String typeName;

    private Map<String, String> attributes;

    private Integer version;

    private List<PropertyVO> properties;

}
