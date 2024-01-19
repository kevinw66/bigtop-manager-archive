package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.HostDTO;
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
}
