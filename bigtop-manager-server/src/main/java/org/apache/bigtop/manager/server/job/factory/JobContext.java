package org.apache.bigtop.manager.server.job.factory;

import lombok.Data;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;

import java.util.List;

@Data
public class JobContext {

    private ClusterDTO clusterDTO;

    private CommandDTO commandDTO;

    private Long clusterId;

    private List<String> hostnames;
}
