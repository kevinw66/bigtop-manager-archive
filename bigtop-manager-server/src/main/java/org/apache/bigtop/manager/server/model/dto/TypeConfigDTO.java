package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TypeConfigDTO implements Serializable {

    private String typeName;

    private Integer version;

    private List<PropertyDTO> properties;

}
