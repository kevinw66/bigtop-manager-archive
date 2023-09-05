package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.ConfigurationDTO;
import org.apache.bigtop.manager.server.model.vo.ConfigurationVO;

import java.util.List;

public interface ConfigurationService {

    /**
     * Get all version configurations.
     *
     * @return configurations
     */
    List<ConfigurationVO> list(String clusterName);

    /**
     * Get all latest configurations.
     *
     * @return configurations
     */
    List<ConfigurationVO> latest(String clusterName);

    /**
     * Update configurations.
     *
     * @return configurations
     */
    List<ConfigurationVO> update(String clusterName, List<ConfigurationDTO> configurationDTOList);
}
