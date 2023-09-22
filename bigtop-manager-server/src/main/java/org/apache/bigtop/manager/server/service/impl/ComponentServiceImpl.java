package org.apache.bigtop.manager.server.service.impl;

import com.google.common.eventbus.EventBus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.RequestState;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.mapper.CommandMapper;
import org.apache.bigtop.manager.server.model.mapper.ComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.HostComponentMapper;
import org.apache.bigtop.manager.server.model.mapper.RequestMapper;
import org.apache.bigtop.manager.server.model.vo.ComponentVO;
import org.apache.bigtop.manager.server.model.vo.HostComponentVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.apache.bigtop.manager.server.orm.entity.Request;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.ComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.HostComponentRepository;
import org.apache.bigtop.manager.server.orm.repository.RequestRepository;
import org.apache.bigtop.manager.server.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private RequestRepository requestRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private HostComponentRepository hostComponentRepository;

    @Resource
    private EventBus eventBus;

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
        Component component = componentRepository.findById(id).orElseThrow(() -> new ServerException(ServerExceptionStatus.COMPONENT_NOT_FOUND));
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

        eventBus.post(CommandMapper.INSTANCE.DTO2Event(commandDTO));

        //persist request to database
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());
        Request request = RequestMapper.INSTANCE.DTO2Entity(commandDTO, cluster);
        request.setState(RequestState.PENDING.name());
        request = requestRepository.save(request);

        return RequestMapper.INSTANCE.Entity2VO(request);
    }
}
