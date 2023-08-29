package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.ServiceVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;

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

    /**
     * execute command for services
     * @param commandDTO {@link CommandDTO}
     * @return {@link CommandVO}
     */
    CommandVO command(CommandDTO commandDTO);

}
