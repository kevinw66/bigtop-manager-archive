package org.apache.bigtop.manager.server.utils;

public class ClusterUtils {

    public static Boolean isNoneCluster(Long clusterId) {
        return clusterId == null || clusterId == 0L;
    }
}
