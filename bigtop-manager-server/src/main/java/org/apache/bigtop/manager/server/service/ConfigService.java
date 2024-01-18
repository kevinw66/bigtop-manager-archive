package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.ServiceConfigDTO;
import org.apache.bigtop.manager.server.model.dto.TypeConfigDTO;
import org.apache.bigtop.manager.server.model.vo.ServiceConfigVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;

import java.util.List;

public interface ConfigService {

    /**
     * Get all version configurations.
     *
     * @return configurations
     */
    List<ServiceConfigVO> list(Long clusterId);

    /**
     * Get all latest configurations.
     *
     * @return configurations
     */
    List<ServiceConfigVO> latest(Long clusterId);

    void upsert(Long clusterId, Long serviceId, List<TypeConfigDTO> configs);

    void updateConfig(Cluster cluster, ServiceConfigDTO serviceConfigDTO);
}
