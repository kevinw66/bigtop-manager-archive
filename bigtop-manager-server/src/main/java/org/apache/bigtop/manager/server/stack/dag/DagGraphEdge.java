package org.apache.bigtop.manager.server.stack.dag;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DagGraphEdge {

    private ComponentCommandWrapper startNode;

    private ComponentCommandWrapper endNode;
}
