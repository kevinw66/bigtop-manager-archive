package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.vo.StackVO;

import java.util.List;

public interface StackService {

    /**
     * Get all stacks.
     *
     * @return Stacks
     */
    List<StackVO> list();
}
