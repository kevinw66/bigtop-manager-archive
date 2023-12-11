package org.apache.bigtop.manager.server.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PropertyDTO implements Serializable {

    private String name;

    private String value;

    private String displayName;

    private String desc;

}
