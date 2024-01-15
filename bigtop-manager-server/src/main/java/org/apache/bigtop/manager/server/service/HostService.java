package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.model.vo.HostVO;

import java.util.List;

public interface HostService {

    /**
     * Get all hosts.
     *
     * @return Hosts
     */
    List<HostVO> list(Long clusterId);

    /**
     * Create a host
     * TODO need to move to command service with host level command
     *
     * @return Host
     */
    CommandVO create(Long clusterId, List<String> hostnames);

    /**
     * Save a host
     *
     * @return Host
     */
    List<HostVO> batchSave(Long clusterId, List<String> hostnames);

    /**
     * Get a host
     *
     * @return Host
     */
    HostVO get(Long id);

    /**
     * Update a host
     *
     * @return Host
     */
    HostVO update(Long id, HostDTO hostDTO);

    /**
     * Delete a host
     *
     * @return Host
     */
    Boolean delete(Long id);

    /**
     * Cache a host
     *
     * @param clusterId cluster id
     * @return boolean
     */
    Boolean cache(Long clusterId);

}
