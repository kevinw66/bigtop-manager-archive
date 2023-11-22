package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.vo.CommandVO;

public interface CommandService {

    /**
     * execute command for a cluster
     * @param commandDTO {@link CommandDTO}
     * @return {@link CommandVO}
     */
    CommandVO command(CommandDTO commandDTO);
}
