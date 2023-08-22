package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.vo.ServiceVersionVO;
import org.apache.bigtop.manager.server.model.vo.StackRepoVO;
import org.apache.bigtop.manager.server.model.vo.StackVO;

import java.util.List;

public interface StackService {

    /**
     * Get all stacks.
     *
     * @return Stacks
     */
    List<StackVO> list();

    /**
     * Get a stack
     *
     * @return Stack
     */
    StackVO get(Long id);

    List<ServiceVersionVO> versions(String stackName, String stackVersion);

    List<StackRepoVO> repos(String stackName, String stackVersion);

}
