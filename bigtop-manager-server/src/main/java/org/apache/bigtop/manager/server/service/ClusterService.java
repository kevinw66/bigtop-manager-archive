package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.model.vo.CommandVO;

import java.util.List;

public interface ClusterService {

    /**
     * Get all clusters.
     *
     * @return Clusters
     */
    List<ClusterVO> list();

    /**
     * Create a cluster
     * TODO need to move to command service with cluster level command
     *
     * @return Cluster
     */
    CommandVO create(ClusterDTO clusterDTO);

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
