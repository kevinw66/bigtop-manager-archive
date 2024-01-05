package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ConfigDataReq {

    @NotBlank
    @Schema(example = "zoo.cfg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String typeName;

    @Schema(example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer version;

    @NotEmpty
    @Schema(example = "{\"name\":\"clientPort\",\"value\": \"2181\"}")
    private List<@Valid PropertyReq> properties;

}
