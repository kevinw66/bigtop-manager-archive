package org.apache.bigtop.manager.server.model.req.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ComponentCommandReq extends AbstractCommandReq {

    @NotEmpty
    @Schema(example = "[\"ZOOKEEPER_SERVER\"]")
    private List<String> componentNames;

    @NotEmpty
    @Schema(example = "ZOOKEEPER")
    private String serviceName;

}
