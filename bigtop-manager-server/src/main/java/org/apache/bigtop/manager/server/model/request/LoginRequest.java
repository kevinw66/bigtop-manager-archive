package org.apache.bigtop.manager.server.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {

    @Schema(example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
