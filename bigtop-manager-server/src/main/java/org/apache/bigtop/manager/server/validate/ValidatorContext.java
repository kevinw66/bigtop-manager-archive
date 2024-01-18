package org.apache.bigtop.manager.server.validate;

import lombok.Data;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;

import java.util.List;

@Data
public class ValidatorContext {

    private CommandDTO commandDTO;

    private ClusterDTO clusterDTO;

    private List<String> hostnames;
}
