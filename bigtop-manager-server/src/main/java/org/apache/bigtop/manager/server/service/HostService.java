package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;

import java.util.List;

public interface HostService {

    /**
     * Get all hosts.
     *
     * @return Hosts
     */
    List<HostVO> list();

    /**
     * Create a host
     *
     * @return Host
     */
    HostVO create(HostDTO hostDTO);

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
     * @param clusterId cluster id
     * @return boolean
     */
    Boolean cache(Long clusterId);

    /**
     * execute command for a host
     * @param commandDTO {@link CommandDTO}
     * @return {@link CommandVO}
     */
    CommandVO command(CommandDTO commandDTO);
}
