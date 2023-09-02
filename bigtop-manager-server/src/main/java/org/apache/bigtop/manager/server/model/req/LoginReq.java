package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginReq {

    @Schema(example = "admin")
    private String username;

    @Schema(example = "admin")
    private String password;
}
