package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.model.vo.PageVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface HostService {

    /**
     * Get all hosts.
     *
     * @return Hosts
     */
    PageVO<HostVO> list(Long clusterId);

    /**
     * Create a host
     *
     * @return Host
     */
    CommandVO create(Long clusterId, List<String> hostnames);

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
     * Get a component by id.
     *
     * @return component
     */
    List<HostComponentVO> hostComponent(Long id);

    /**
     * Cache a host
     *
     * @param clusterId cluster id
     * @return boolean
     */
    Boolean cache(Long clusterId);

}
