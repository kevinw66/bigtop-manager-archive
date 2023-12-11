package org.apache.bigtop.manager.server.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ConfigDataReq {

    @NotBlank
    @Schema(example = "zoo.cfg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String typeName;

    @Schema(example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "如果进行回滚操作，需要指定版本号")
    private Integer version;

    @Schema(example = "{\"supports_adding_forbidden\": \"true\"}")
    private Map<String, String> attributes;

    @NotEmpty
    @Schema(example = "{\"name\":\"clientPort\",\"value\": \"2181\"}")
    private List<@Valid PropertyReq> properties;

}
