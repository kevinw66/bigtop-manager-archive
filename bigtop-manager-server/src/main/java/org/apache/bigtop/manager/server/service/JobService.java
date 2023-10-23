package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.vo.JobVO;
import org.apache.bigtop.manager.server.model.vo.PageVO;

public interface JobService {

    PageVO<JobVO> list();

    JobVO get(Long id);
}
