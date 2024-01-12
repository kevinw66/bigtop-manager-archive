package org.apache.bigtop.manager.server.listener.factory;

import lombok.Data;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;

import java.util.List;

@Data
public class JobFactoryContext {

    private ClusterDTO clusterDTO;

    private CommandDTO commandDTO;

    private Long clusterId;

    private List<String> hostnames;
}
