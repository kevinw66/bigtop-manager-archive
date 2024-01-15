package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;

import java.util.List;

public interface HostComponentService {

    /**
     * Get all host-components.
     *
     * @return host-components
     */
    List<HostComponentVO> list(Long clusterId);

    /**
     * Get all host-components.
     *
     * @return host-components
     */
    List<HostComponentVO> listByHost(Long clusterId, Long hostId);

    /**
     * Get all host-components.
     *
     * @return host-components
     */
    List<HostComponentVO> listByService(Long clusterId, Long serviceId);

    void batchSave(Long clusterId, String hostname, List<String> componentNames);

    void saveByCommand(CommandDTO commandDTO);

}
