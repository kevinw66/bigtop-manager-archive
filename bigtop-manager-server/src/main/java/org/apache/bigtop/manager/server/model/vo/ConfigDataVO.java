package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ConfigDataVO {

    private String typeName;

    private Integer version;

    private Map<String, Object> configData;

    private Map<String, Map<String, Object>> configAttributes;
}
