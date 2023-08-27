package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;

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
     *
     * @return Cluster
     */
    ClusterVO create(ClusterDTO clusterDTO);

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

    /**
     * Delete a cluster
     *
     * @return Cluster
     */
    Boolean delete(Long id);

    /**
     * execute command for a cluster
     * @param commandDTO {@link CommandDTO}
     * @return {@link CommandVO}
     */
    CommandVO command(CommandDTO commandDTO);
}
