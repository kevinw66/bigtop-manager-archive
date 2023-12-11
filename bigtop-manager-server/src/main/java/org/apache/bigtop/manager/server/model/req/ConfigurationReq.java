package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ConfigurationReq {

    @NotBlank
    @Schema(example = "ZOOKEEPER")
    private String serviceName;

    @Schema(example = "Initial Zookeeper configuration")
    private String configDesc;

    private List<@Valid ConfigDataReq> configurations;
}

