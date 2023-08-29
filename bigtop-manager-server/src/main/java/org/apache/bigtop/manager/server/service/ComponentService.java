package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.vo.ComponentVO;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;

import java.util.List;

public interface ComponentService {

    /**
     * Get all components.
     *
     * @return components
     */
    List<ComponentVO> list();

    /**
     * Get a component by id.
     *
     * @return component
     */
    ComponentVO get(Long id);

    /**
     * Get a component by id.
     *
     * @return component
     */
    List<HostComponentVO> hostComponent(Long id);

    /**
     * execute command for components
     * @param commandDTO {@link CommandDTO}
     * @return {@link CommandVO}
     */
    CommandVO command(CommandDTO commandDTO);
}
