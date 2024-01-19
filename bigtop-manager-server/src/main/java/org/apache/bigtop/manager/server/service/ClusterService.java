package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;

import java.util.List;

public interface ClusterService {

    /**
     * Get all clusters.
     *
     * @return Clusters
     */
    List<ClusterVO> list();

    /**
     * Save a cluster
     *
     * @return Cluster
     */
    ClusterVO save(ClusterDTO clusterDTO);

    /**
     * Get a cluster
     *
     * @return Cluster
     */
    ClusterVO get(Long id);

    /**
     * Update a cluster
     *
     * @return Cluster
     */
    ClusterVO update(Long id, ClusterDTO clusterDTO);

}
