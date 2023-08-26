package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.RequestState;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.*;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.model.mapper.RequestMapper;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.model.vo.command.CommandVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.apache.bigtop.manager.server.orm.entity.Request;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.RepoRepository;
import org.apache.bigtop.manager.server.orm.repository.RequestRepository;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.apache.bigtop.manager.server.ws.TaskFlowHandler;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@org.springframework.stereotype.Service
public class ClusterServiceImpl implements ClusterService {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private StackRepository stackRepository;

    @Resource
    private RepoRepository repoRepository;

    @Resource
    private RequestRepository requestRepository;

    @Resource
    private TaskFlowHandler taskFlowHandler;

    @Override
    public List<ClusterVO> list() {
        List<ClusterVO> clusterVOList = new ArrayList<>();
        clusterRepository.findAll().forEach(cluster -> {
            ClusterVO clusterVO = ClusterMapper.INSTANCE.Entity2VO(cluster);
            clusterVOList.add(clusterVO);
        });

        return clusterVOList;
    }

    @Override
    public ClusterVO create(ClusterDTO clusterDTO) {
        String stackName = clusterDTO.getStackName();
        String stackVersion = clusterDTO.getStackVersion();
        Map<String, ImmutablePair<StackDTO, Set<ServiceDTO>>> stackKeyMap = StackUtils.getStackKeyMap();
        ImmutablePair<StackDTO, Set<ServiceDTO>> immutablePair = stackKeyMap.get(StackUtils.fullStackName(stackName, stackVersion));
        StackDTO stackDTO = immutablePair.getLeft();

        // save cluster
        Stack stack = stackRepository.findByStackNameAndStackVersion(stackName, stackVersion).orElse(new Stack());
        if (stack.getId() == null) {
            throw new ServerException(ServerExceptionStatus.STACK_NOT_FOUND);
        }
        Cluster cluster = clusterRepository.findByClusterName(clusterDTO.getClusterName()).orElse(new Cluster());
        if (cluster.getId() == null) {
            cluster = ClusterMapper.INSTANCE.DTO2Entity(clusterDTO, stackDTO, stack);

            cluster = clusterRepository.save(cluster);
        }
        log.info("stack: {}, cluster: {}", stack, cluster);

        //Update repos if isPresent
        List<RepoDTO> repoDTOList = clusterDTO.getRepoInfoList();
        if (!CollectionUtils.isEmpty(repoDTOList)) {
            for (RepoDTO repoDTO : repoDTOList) {
                Repo repo = RepoMapper.INSTANCE.DTO2Entity(repoDTO, stack);

                Optional<Repo> repoOptional = repoRepository.findByRepoIdAndOsAndArchAndStackId(repo.getRepoId(), repo.getOs(), repo.getArch(), stack.getId());

                repoOptional.ifPresent(value -> repo.setId(value.getId()));
                repoRepository.save(repo);
            }
        }

        return ClusterMapper.INSTANCE.Entity2VO(cluster);
    }

    @Override
    public ClusterVO get(Long id) {
        Cluster cluster = clusterRepository.findById(id).orElseThrow(() -> new ServerException(ServerExceptionStatus.CLUSTER_NOT_FOUND));

        return ClusterMapper.INSTANCE.Entity2VO(cluster);
    }

    @Override
    public ClusterVO update(Long id, ClusterDTO clusterDTO) {
        Cluster cluster = ClusterMapper.INSTANCE.DTO2Entity(clusterDTO);
        cluster.setId(id);
        clusterRepository.save(cluster);

        return ClusterMapper.INSTANCE.Entity2VO(cluster);
    }

    @Override
    public Boolean delete(Long id) {
        clusterRepository.deleteById(id);
        return true;
    }

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
