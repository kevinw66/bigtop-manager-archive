package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.pojo.stack.RepoInfo;
import org.apache.bigtop.manager.common.pojo.stack.ServiceInfo;
import org.apache.bigtop.manager.common.pojo.stack.StackInfo;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.ClusterDTO;
import org.apache.bigtop.manager.server.model.mapper.ClusterMapper;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.model.vo.ClusterVO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.apache.bigtop.manager.server.orm.repository.RepoRepository;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.service.ClusterService;
import org.apache.bigtop.manager.server.stack.StackInitialization;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class ClusterServiceImpl implements ClusterService {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private StackRepository stackRepository;

    @Resource
    private RepoRepository repoRepository;

    @Resource
    private StackInitialization stackInitialization;

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

        String fullStackName = StackUtils.fullStackName(stackName, stackVersion);

        ImmutablePair<StackInfo, Set<ServiceInfo>> stackTuple = stackInitialization.getStackKeyMap().get(fullStackName);
        // save cluster
        Stack stack = stackRepository.findByStackNameAndStackVersion(stackName, stackVersion).orElse(new Stack());
        if (stack.getId() == null) {
            throw new ServerException(ServerExceptionStatus.STACK_NOT_FOUND);
        }
        Cluster cluster = clusterRepository.findByClusterName(clusterDTO.getClusterName()).orElse(new Cluster());
        if (cluster.getId() == null) {
            cluster = ClusterMapper.INSTANCE.DTO2Entity(clusterDTO, stackTuple.left);
            cluster.setStack(stack);

            clusterRepository.save(cluster);
            cluster = clusterRepository.findByClusterName(clusterDTO.getClusterName()).orElse(new Cluster());
        }
        log.info("stack: {}, cluster: {}", stack, cluster);

        List<RepoInfo> repoInfoList = clusterDTO.getRepoInfoList();
        if (!CollectionUtils.isEmpty(repoInfoList)) {
            for (RepoInfo repoInfo : repoInfoList) {

                Repo repo = RepoMapper.INSTANCE.POJO2Entity(repoInfo, stack);

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
}
