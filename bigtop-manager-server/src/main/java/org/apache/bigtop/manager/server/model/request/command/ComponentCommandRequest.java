package org.apache.bigtop.manager.server.model.request.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ComponentCommandRequest  extends AbstractCommandRequest {

    @Schema(example = "[\"ZOOKEEPER_SERVER\"]")
    private List<String> componentNames;

    @Schema(example = "ZOOKEEPER")
    private String serviceName;

}
