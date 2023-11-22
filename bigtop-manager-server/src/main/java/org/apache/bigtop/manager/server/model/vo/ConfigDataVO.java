package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;
import org.apache.bigtop.manager.server.model.dto.PropertyDTO;

import java.util.Map;

@Data
public class ConfigDataVO {

    private String typeName;

    private Map<String, String> attributes;

    private Integer version;

    private Map<String, Object> configData;

    private Map<String, PropertyDTO> configAttributes;
}
