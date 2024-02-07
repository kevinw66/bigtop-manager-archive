package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.query.PageQuery;
import org.apache.bigtop.manager.server.model.vo.JobVO;
import org.apache.bigtop.manager.server.model.vo.PageVO;
import org.apache.bigtop.manager.dao.entity.Job;
import org.apache.bigtop.manager.dao.repository.JobRepository;
import org.apache.bigtop.manager.server.service.JobService;
import org.apache.bigtop.manager.server.utils.ClusterUtils;
import org.apache.bigtop.manager.server.utils.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Resource
    private JobRepository jobRepository;

    @Override
    public List<JobVO> list(Long clusterId) {
//        PageQuery pageQuery = PageUtils.getPageQuery();
//        Pageable pageable = PageRequest.of(pageQuery.getPageNum(), pageQuery.getPageSize(), pageQuery.getSort());
//        Page<Job> page;
//        if (ClusterUtils.isNoneCluster(clusterId)) {
//            page = jobRepository.findAllByClusterIsNull(pageable);
//        } else {
//            page = jobRepository.findAllByClusterId(clusterId, pageable);
//        }

        List<Job> jobs = jobRepository.findAllByClusterId(clusterId);

        return JobMapper.INSTANCE.fromEntity2VO(jobs);
    }

    @Override
    public JobVO get(Long id) {
        Job job = jobRepository.getReferenceById(id);
        return JobMapper.INSTANCE.fromEntity2VO(job);
    }
}
