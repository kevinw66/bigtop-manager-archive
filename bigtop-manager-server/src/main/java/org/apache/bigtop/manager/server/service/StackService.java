package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.vo.ConfigDataVO;
import org.apache.bigtop.manager.server.model.vo.StackComponentVO;
import org.apache.bigtop.manager.server.model.vo.StackVO;

import java.util.List;
import java.util.Map;

public interface StackService {

    /**
     * Get all stacks.
     *
     * @return Stacks
     */
    List<StackVO> list();

    List<StackComponentVO> components(String stackName, String stackVersion);

    Map<String, List<ConfigDataVO>> configurations(String stackName, String stackVersion);
}
