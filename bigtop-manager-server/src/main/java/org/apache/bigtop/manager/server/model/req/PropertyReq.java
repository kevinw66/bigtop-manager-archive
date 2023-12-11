package org.apache.bigtop.manager.server.model.req;

import lombok.Data;

@Data
public class PropertyReq {

    private String name;

    private String value;

    private String displayName;

    private String desc;

}
