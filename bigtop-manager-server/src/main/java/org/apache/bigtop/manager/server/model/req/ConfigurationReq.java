package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ConfigurationReq {

    @Schema(example = "ZOOKEEPER")
    private String serviceName;

    @Schema(example = "Initial Zookeeper configuration")
    private String configDesc;

    private List<ConfigDataReq> configurations;
}

