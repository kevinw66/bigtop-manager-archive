package org.apache.bigtop.manager.server.model.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PropertyReq {

    @NotBlank
    private String name;

    private String value;

    private String displayName;

    private String desc;

}
