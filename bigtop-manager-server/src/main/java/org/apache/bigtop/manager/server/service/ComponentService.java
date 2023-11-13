package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.vo.ComponentVO;

import java.util.List;

public interface ComponentService {

    /**
     * Get all components.
     *
     * @return components
     */
    List<ComponentVO> list(Long clusterId);

    /**
     * Get a component by id.
     *
     * @return component
     */
    ComponentVO get(Long id);

}
