package org.apache.bigtop.manager.server.service.impl;

import com.google.common.eventbus.EventBus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.event.CommandEvent;
import org.apache.bigtop.manager.server.model.mapper.CommandMapper;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.HostComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.JobMapper;
import org.apache.bigtop.manager.server.model.vo.ComponentVO;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.ComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.publisher.EventPublisher;
import org.apache.bigtop.manager.server.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Override
    public List<ComponentVO> list() {
        List<ComponentVO> componentVOList = new ArrayList<>();
        componentRepository.findAll().forEach(stack -> {
            ComponentVO componentVO = ComponentMapper.INSTANCE.Entity2VO(stack);
            componentVOList.add(componentVO);
        });

        return componentVOList;
    }

    @Override
    public ComponentVO get(Long id) {
        Component component = componentRepository.findById(id).orElseThrow(() -> new ApiException(ApiExceptionEnum.COMPONENT_NOT_FOUND));
        return ComponentMapper.INSTANCE.Entity2VO(component);
    }

    @Override
    public List<HostComponentVO> hostComponent(Long id) {
        List<HostComponent> hostComponentList = hostComponentRepository.findAllByComponentId(id);
        return HostComponentMapper.INSTANCE.Entity2VO(hostComponentList);
    }

    @Override
    public CommandVO command(CommandDTO commandDTO) {
        String clusterName = commandDTO.getClusterName();
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());

        //persist request to database
        Job job = JobMapper.INSTANCE.DTO2Entity(commandDTO, cluster);
        job = jobRepository.save(job);

        CommandEvent commandEvent = CommandMapper.INSTANCE.DTO2Event(commandDTO, job);
        EventPublisher.publish(commandEvent);

        return JobMapper.INSTANCE.Entity2VO(job);
    }
}
