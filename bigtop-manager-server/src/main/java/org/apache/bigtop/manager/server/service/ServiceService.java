package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.vo.ServiceVO;

import java.util.List;

public interface ServiceService {

    /**
     * Get all stacks.
     *
     * @return Stacks
     */
    List<ServiceVO> list(Long clusterId);

    /**
     * Get a service.
     *
     * @return service
     */
    ServiceVO get(Long id);

    void saveByCommand(CommandDTO commandDTO);
}
