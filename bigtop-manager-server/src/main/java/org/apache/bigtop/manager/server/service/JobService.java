package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.vo.JobVO;
import org.apache.bigtop.manager.server.model.vo.PageVO;

public interface JobService {

    PageVO<JobVO> list(Long clusterId);

    JobVO get(Long id);
}
