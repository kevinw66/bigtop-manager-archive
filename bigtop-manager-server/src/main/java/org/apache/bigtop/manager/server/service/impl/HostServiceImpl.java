package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.listener.factory.HostAddJobFactory;
import org.apache.bigtop.manager.server.listener.factory.HostCacheJobFactory;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.event.HostAddEvent;
import org.apache.bigtop.manager.server.model.event.HostCacheEvent;
import org.apache.bigtop.manager.server.model.mapper.HostMapper;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.query.PageQuery;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.model.vo.PageVO;
import org.apache.bigtop.manager.server.model.vo.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.bigtop.manager.server.utils.PageUtils;
import org.apache.bigtop.manager.server.validate.HostAddValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public PageVO<HostVO> list(Long clusterId) {
        PageQuery pageQuery = PageUtils.getPageQuery();
        Pageable pageable = PageRequest.of(pageQuery.getPageNum(), pageQuery.getPageSize(), pageQuery.getSort());
        Page<Host> page = hostRepository.findAllByClusterId(clusterId, pageable);
        if (CollectionUtils.isEmpty(page.getContent())) {
            throw new ApiException(ApiExceptionEnum.HOST_NOT_FOUND);
        }

        return PageVO.of(page);
    }

    @Override
    @Transactional
    public CommandVO create(Long clusterId, List<String> hostnames) {
        // Check hosts
        hostAddValidator.validate(hostnames);

        Job job = hostAddJobFactory.createJob(clusterId, hostnames);

        HostAddEvent hostAddEvent = new HostAddEvent(hostnames);
        hostAddEvent.setJobId(job.getId());
        hostAddEvent.setHostnames(hostnames);

        SpringContextHolder.getApplicationContext().publishEvent(hostAddEvent);

        return JobMapper.INSTANCE.Entity2CommandVO(job);
    }

    @Override
    public HostVO get(Long id) {
        Host host = hostRepository.findById(id).orElseThrow(() -> new ApiException(ApiExceptionEnum.HOST_NOT_FOUND));

        return HostMapper.INSTANCE.Entity2VO(host);
    }

    @Override
    public HostVO update(Long id, HostDTO hostDTO) {
        Host host = HostMapper.INSTANCE.DTO2Entity(hostDTO);
        host.setId(id);
        hostRepository.save(host);

        return HostMapper.INSTANCE.Entity2VO(host);
    }

    @Override
    public Boolean delete(Long id) {
        hostRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public Boolean cache(Long clusterId) {
        Job job = hostCacheJobFactory.createJob(clusterId);

        HostCacheEvent hostCacheEvent = new HostCacheEvent(clusterId);
        hostCacheEvent.setJobId(job.getId());
        SpringContextHolder.getApplicationContext().publishEvent(hostCacheEvent);
        return true;
    }

}
