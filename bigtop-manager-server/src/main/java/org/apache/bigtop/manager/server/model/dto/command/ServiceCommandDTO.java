package org.apache.bigtop.manager.server.model.dto.command;

import lombok.Data;
import org.apache.bigtop.manager.server.model.dto.ComponentHostDTO;
import org.apache.bigtop.manager.server.model.dto.TypeConfigDTO;

import java.io.Serializable;
import java.util.List;

@Data
public class ServiceCommandDTO implements Serializable {

    private String serviceName;

    private String configDesc;

    private Integer version;

    private List<ComponentHostDTO> componentHosts;

    private List<TypeConfigDTO> configs;
}
