package org.apache.bigtop.manager.server.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ComponentHostDTO implements Serializable {

    private String componentName;

    @NotEmpty
    private List<String> hostnames;
}
