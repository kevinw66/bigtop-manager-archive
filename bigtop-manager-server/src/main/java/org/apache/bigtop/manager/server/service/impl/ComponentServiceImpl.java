package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.RequestState;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;
import org.apache.bigtop.manager.server.model.mapper.RequestMapper;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Request;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.RequestRepository;
import org.apache.bigtop.manager.server.service.ComponentService;
import org.apache.bigtop.manager.server.ws.TaskFlowHandler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private RequestRepository requestRepository;

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private TaskFlowHandler taskFlowHandler;


    @Override
    public CommandVO command(CommandDTO commandDTO) {
        String clusterName = commandDTO.getClusterName();

        taskFlowHandler.submitTaskFlow(commandDTO);

        //persist request to database
        Cluster cluster = clusterRepository.findByClusterName(clusterName).orElse(new Cluster());
        Request request = RequestMapper.INSTANCE.DTO2Entity(commandDTO, cluster);
        request.setState(RequestState.PENDING.name());
        request = requestRepository.save(request);

        return RequestMapper.INSTANCE.Entity2VO(request);
    }
}
