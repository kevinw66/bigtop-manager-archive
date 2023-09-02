package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ConfigDataDTO {

    private String typeName;

    private Integer version;

    private Map<String, Object> configData;

    private Map<String, Map<String, Object>> configAttributes;
}
