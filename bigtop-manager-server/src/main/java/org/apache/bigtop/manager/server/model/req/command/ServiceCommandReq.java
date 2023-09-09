package org.apache.bigtop.manager.server.model.req.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceCommandReq extends AbstractCommandReq {

    @NotEmpty
    @Schema(example = "[\"ZOOKEEPER\"]")
    private List<String> serviceNames;

    @Schema(example = "{\"ZOOKEEPER_SERVER\": [\"node1\"]}")
    private Map<String, Set<String>> componentHosts;

}
