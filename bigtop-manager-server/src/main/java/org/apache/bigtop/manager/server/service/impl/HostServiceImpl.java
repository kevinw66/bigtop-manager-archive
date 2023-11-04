package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.listener.operator.HostAddJobFactory;
import org.apache.bigtop.manager.server.model.dto.HostDTO;
import org.apache.bigtop.manager.server.model.event.HostAddEvent;
import org.apache.bigtop.manager.server.model.event.HostCacheEvent;
import org.apache.bigtop.manager.server.model.mapper.HostComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.HostMapper;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.query.PageQuery;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.HostVO;
import org.apache.bigtop.manager.server.model.vo.PageVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Host;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.HostRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.publisher.EventPublisher;
import org.apache.bigtop.manager.server.service.HostService;
import org.apache.bigtop.manager.server.utils.PageUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HostServiceImpl implements HostService {

    @Resource
    private HostRepository hostRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private JobRepository jobRepository;

    @Resource
    private HostAddJobFactory hostAddJobFactory;

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
    public CommandVO create(Long clusterId, List<String> hostnames) {
        Cluster cluster = clusterRepository.getReferenceById(clusterId);

        Job job = hostAddJobFactory.createJob(clusterId, hostnames);
        jobRepository.save(job);

        HostAddEvent hostAddEvent = new HostAddEvent();
        hostAddEvent.setHostnames(hostnames);
        hostAddEvent.setClusterId(cluster.getId());
        hostAddEvent.setJobId(job.getId());
        EventPublisher.publish(hostAddEvent);

        cache(cluster.getId());

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
    public List<HostComponentVO> hostComponent(Long id) {
        List<HostComponent> hostComponentList = hostComponentRepository.findAllByHostId(id);
        return HostComponentMapper.INSTANCE.Entity2VO(hostComponentList);
    }

    @Override
    public Boolean cache(Long clusterId) {
        EventPublisher.publish(new HostCacheEvent(clusterId));
        return true;
    }

}
