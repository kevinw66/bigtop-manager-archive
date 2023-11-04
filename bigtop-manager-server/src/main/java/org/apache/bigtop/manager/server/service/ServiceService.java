package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.ServiceVO;

import java.util.List;

public interface ServiceService {

    /**
     * Get all stacks.
     *
     * @return Stacks
     */
    List<ServiceVO> list();

    /**
     * Get a service.
     *
     * @return service
     */
    ServiceVO get(Long id);

    /**
     * Get a component by id.
     *
     * @return component
     */
    List<HostComponentVO> hostComponent(Long id);

}
