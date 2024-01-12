package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.listener.factory.HostAddJobFactory;
import org.apache.bigtop.manager.server.listener.factory.HostCacheJobFactory;
import org.apache.bigtop.manager.server.listener.factory.JobFactoryContext;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.event.HostAddEvent;
import org.apache.bigtop.manager.server.model.event.HostCacheEvent;
import org.apache.bigtop.manager.server.model.mapper.HostMapper;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.bigtop.manager.server.validate.HostAddValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class HostServiceImpl implements HostService {

    @Resource
    private HostRepository hostRepository;

    @Resource
    private HostAddJobFactory hostAddJobFactory;

    @Resource
    private HostCacheJobFactory hostCacheJobFactory;

    @Resource
    private HostAddValidator hostAddValidator;

    @Override
    public List<HostVO> list(Long clusterId) {
        List<Host> hosts = hostRepository.findAllByClusterId(clusterId);
        if (CollectionUtils.isEmpty(hosts)) {
            throw new ApiException(ApiExceptionEnum.HOST_NOT_FOUND);
        }

        return HostMapper.INSTANCE.fromEntity2VO(hosts);
    }

    @Override
    @Transactional
    public CommandVO create(Long clusterId, List<String> hostnames) {
        // Check hosts
        hostAddValidator.validate(hostnames);

        JobFactoryContext jobFactoryContext = new JobFactoryContext();
        jobFactoryContext.setClusterId(clusterId);
        jobFactoryContext.setHostnames(hostnames);
        Job job = hostAddJobFactory.createJob(jobFactoryContext);

        HostAddEvent hostAddEvent = new HostAddEvent(hostnames);
        hostAddEvent.setJobId(job.getId());
        hostAddEvent.setHostnames(hostnames);

        SpringContextHolder.getApplicationContext().publishEvent(hostAddEvent);

        return JobMapper.INSTANCE.fromEntity2CommandVO(job);
    }

    @Override
    public HostVO get(Long id) {
        Host host = hostRepository.findById(id).orElseThrow(() -> new ApiException(ApiExceptionEnum.HOST_NOT_FOUND));

        return HostMapper.INSTANCE.fromEntity2VO(host);
    }

    @Override
    public HostVO update(Long id, HostDTO hostDTO) {
        Host host = HostMapper.INSTANCE.fromDTO2Entity(hostDTO);
        host.setId(id);
        hostRepository.save(host);

        return HostMapper.INSTANCE.fromEntity2VO(host);
    }

    @Override
    public Boolean delete(Long id) {
        hostRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public Boolean cache(Long clusterId) {
        JobFactoryContext jobFactoryContext = new JobFactoryContext();
        jobFactoryContext.setClusterId(clusterId);
        Job job = hostCacheJobFactory.createJob(jobFactoryContext);

        HostCacheEvent hostCacheEvent = new HostCacheEvent(clusterId);
        hostCacheEvent.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(hostCacheEvent);
        return true;
    }

}
