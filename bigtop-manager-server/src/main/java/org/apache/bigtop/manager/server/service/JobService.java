package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.vo.JobVO;

import java.util.List;

public interface JobService {

    List<JobVO> list();

    JobVO get(Long id);
}
