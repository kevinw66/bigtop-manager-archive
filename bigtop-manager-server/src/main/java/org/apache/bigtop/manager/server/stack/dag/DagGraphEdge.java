package org.apache.bigtop.manager.server.stack.dag;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DagGraphEdge {

    private String startNode;

    private String endNode;

    @Override
    public String toString() {
        return startNode + " -> " + endNode;
    }
}
