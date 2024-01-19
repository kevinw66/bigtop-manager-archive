package org.apache.bigtop.manager.server.job.factory;

import lombok.Data;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;

@Data
public class JobContext {

    private CommandDTO commandDTO;
}
