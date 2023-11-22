package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.ConfigurationDTO;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.model.vo.ConfigurationVO;

import java.util.List;

public interface ConfigurationService {

    /**
     * Get all version configurations.
     *
     * @return configurations
     */
    List<ConfigurationVO> list(Long clusterId);

    /**
     * Get all latest configurations.
     *
     * @return configurations
     */
    List<ConfigurationVO> latest(Long clusterId);

    /**
     * Update configurations.
     *
     * @return configurations
     */
    CommandVO update(Long clusterId, List<ConfigurationDTO> configurationDTOList);
}
