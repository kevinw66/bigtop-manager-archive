package org.apache.bigtop.manager.server.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HostDTO {

    private Long clusterId;

    private String hostname;
}
