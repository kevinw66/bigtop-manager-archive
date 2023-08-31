package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;

public interface ComponentService {

    /**
     * execute command for components
     * @param commandDTO {@link CommandDTO}
     * @return {@link CommandVO}
     */
    CommandVO command(CommandDTO commandDTO);
}
