package org.apache.bigtop.manager.server.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class TypeConfigVO {

    private String typeName;

    private List<PropertyVO> properties;

}
