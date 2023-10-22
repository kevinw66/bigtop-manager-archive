package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.model.vo.JobVO;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.service.JobService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Resource
    private JobRepository jobRepository;

    @Override
    public List<JobVO> list() {
        List<JobVO> jobVOList = new ArrayList<>();
        jobRepository.findAll().forEach(job -> {
            JobVO jobVO = JobMapper.INSTANCE.Entity2VO(job);
            jobVOList.add(jobVO);
        });

        return jobVOList;
    }

    @Override
    public JobVO get(Long id) {
        Job job = jobRepository.getReferenceById(id);
        return JobMapper.INSTANCE.Entity2VO(job);
    }
}
